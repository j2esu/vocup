package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.component.AddWord.DefState
import ru.uxapps.vocup.data.Def
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.util.asStateFlow
import java.io.IOException

interface AddWord {

    val definitions: LiveData<DefState>
    val maxWordLength: Int
    val languages: LiveData<List<Language>>
    fun onInput(text: String)
    fun onSave(item: DefItem)
    fun onRemove(item: DefItem)
    fun onChooseLang(lang: Language)

    sealed class DefState {
        object Idle : DefState()
        object Loading : DefState()
        data class Data(val items: List<DefItem>) : DefState()
        object Error : DefState()
    }

    data class DefItem(val def: Def, val saved: Boolean)
}

class AddWordImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : AddWord {

    companion object {
        private val WORD_RANGE = 2..30
    }

    private val wordInput = MutableStateFlow("")
    private val allWords = repo.getAllWords().asStateFlow(scope)

    override val definitions: LiveData<DefState> =
        wordInput
            .map { normalizeInput(it) }
            .distinctUntilChanged()
            .combine(repo.getTargetLang()) { input, lang -> input to lang }
            .transformLatest { (input, lang) ->
                if (input.length in WORD_RANGE) {
                    emit(DefState.Loading)
                    delay(400)
                    val result = try {
                        repo.getTranslations(input, lang)
                    } catch (e: IOException) {
                        null
                    }
                    if (result != null) {
                        emitAll(allWords.filterNotNull().map { words ->
                            DefState.Data(result.map { def ->
                                DefItem(def, words.any { it.text == def.text })
                            })
                        })
                    } else {
                        emit(DefState.Error)
                    }
                } else {
                    emit(DefState.Idle)
                }
            }
            .asLiveData()

    private fun normalizeInput(input: String) =
        input.trim().replace(Regex("\\s+"), " ")

    override val maxWordLength = WORD_RANGE.last

    override val languages: LiveData<List<Language>> =
        repo.getTargetLang().map {
            listOf(it) + (Language.values().toList() - it)
        }.asLiveData()

    override fun onInput(text: String) {
        wordInput.value = text
    }

    override fun onSave(item: DefItem) {
        scope.launch {
            repo.addWord(item.def)
        }
    }

    override fun onRemove(item: DefItem) {
        scope.launch {
            repo.removeWord(item.def)
        }
    }

    override fun onChooseLang(lang: Language) {
        scope.launch {
            repo.setTargetLang(lang)
        }
    }
}