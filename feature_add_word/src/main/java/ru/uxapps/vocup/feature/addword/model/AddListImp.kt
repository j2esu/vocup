package ru.uxapps.vocup.feature.addword.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.addword.model.AddList.State.*
import ru.uxapps.vocup.util.repeatWhen
import java.io.IOException

internal class AddListImp(
    private val inputList: List<String>,
    private val scope: CoroutineScope,
    private val repo: Repo
) : AddList {

    private val lang = Lang(scope, repo)
    private val saveDef = SaveDef(repo)
    private val load = Channel<Unit>()

    override val state = repo.getTargetLanguage().repeatWhen(load.receiveAsFlow()).transformLatest {
        emit(Loading)
        try {
            val defs = repo.getDefinitions(inputList)
            val itemsFlow = repo.getAllWords().map { words ->
                defs.map { def ->
                    val word = words.find { it.text.equals(def.text, true) }
                    DefItem(def.text, word?.id, def.translations.map { trans ->
                        val saved = word?.translations?.any { it.equals(trans, true) } == true
                        trans to saved
                    })
                }
            }
            emitAll(itemsFlow.map { Data(it) })
        } catch (e: IOException) {
            emit(Error)
        }
    }.asLiveData(scope.coroutineContext + Dispatchers.IO)

    override val languages = lang.languages

    override fun onRetry() {
        load.offer(Unit)
    }

    override fun onSave(item: DefItem) {
        scope.launch { saveDef.run(item) }
    }

    override fun onChooseLang(lang: Language) = this.lang.onChooseLang(lang)
}