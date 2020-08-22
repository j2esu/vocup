package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.component.AddWord.DefState
import ru.uxapps.vocup.data.Def
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.util.combine
import ru.uxapps.vocup.util.repeatWhen
import ru.uxapps.vocup.util.toStateFlow
import java.io.IOException
import java.util.concurrent.TimeUnit

interface AddWord {

    val definitions: LiveData<DefState>
    val maxWordLength: Int
    val languages: LiveData<List<Language>>
    val completions: LiveData<List<String>>
    fun onInput(text: String)
    fun onSave(item: DefItem)
    fun onChooseLang(lang: Language)
    fun onRetry()
    fun onRestoreWord(word: Word)

    sealed class DefState {
        object Idle : DefState()
        object Loading : DefState()
        data class Data(val items: List<DefItem>, val error: Boolean) : DefState()
    }

    data class DefItem(val text: String, val saved: Boolean, val trans: List<Pair<String, Boolean>>?)
}

class AddWordImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : AddWord {

    companion object {
        private val WORD_RANGE = 2..30
    }

    private val wordInput = MutableStateFlow("")
    private val allWords = repo.getAllWords().toStateFlow(scope)
    private val retry = Channel<Unit>()

    override val definitions: LiveData<DefState> =
        wordInput
            .map { normalizeInput(it) }
            .distinctUntilChanged()
            .combine(repo.getTargetLang())
            .repeatWhen(retry.receiveAsFlow())
            .transformLatest { (input, lang) ->
                if (input.length in WORD_RANGE) {
                    emit(DefState.Loading)
                    delay(400)
                    val result = try {
                        repo.getDefinitions(input, lang)
                    } catch (e: IOException) {
                        null
                    }
                    emitAll(allWords.filterNotNull().map { words ->
                        val defs = if (result?.isNotEmpty() == true) result else listOf(Def(input, emptyList()))
                        val items = defs.map { def ->
                            val savedWord = words.find { it.text == def.text }
                            if (result != null) {
                                if (savedWord != null) {
                                    DefItem(def.text, true, def.translations.map {
                                        it to savedWord.translations.contains(it)
                                    })
                                } else {
                                    DefItem(def.text, false, def.translations.map { it to false })
                                }
                            } else {
                                DefItem(def.text, savedWord != null, null)
                            }
                        }
                        DefState.Data(items, result == null)
                    })
                } else {
                    emit(DefState.Idle)
                }
            }
            .asLiveData(timeoutInMs = TimeUnit.MINUTES.toMillis(5))

    private fun normalizeInput(input: String) =
        input.trim().replace(Regex("\\s+"), " ")

    override val maxWordLength = WORD_RANGE.last

    override val completions = wordInput
        .transformLatest {
            emit(emptyList())
            if (it.length >= WORD_RANGE.first) {
                emit(repo.getWordCompletions(it))
            }
        }
        .asLiveData()

    override val languages: LiveData<List<Language>> =
        repo.getTargetLang().map {
            listOf(it) + (Language.values().toList() - it)
        }.asLiveData()

    override fun onInput(text: String) {
        wordInput.value = text
    }

    override fun onSave(item: DefItem) {
        scope.launch {
            if (!item.saved) {
                repo.addWord(item.text, item.trans?.map { it.first } ?: emptyList())
            } else {
                repo.addTranslations(item.text, (item.trans ?: emptyList()).filter { !it.second }.map { it.first })
            }
        }
    }

    override fun onChooseLang(lang: Language) {
        scope.launch {
            repo.setTargetLang(lang)
        }
    }

    override fun onRetry() {
        retry.offer(Unit)
    }

    override fun onRestoreWord(word: Word) {
        scope.launch {
            repo.addWord(word)
        }
    }
}