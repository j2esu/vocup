package ru.uxapps.vocup.feature.addword

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Def
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.addword.AddWord.DefItem
import ru.uxapps.vocup.feature.addword.AddWord.State
import ru.uxapps.vocup.feature.addword.AddWord.State.*
import ru.uxapps.vocup.util.repeatWhen
import ru.uxapps.vocup.util.toStateFlow
import java.io.IOException
import java.util.concurrent.TimeUnit

interface AddWord {

    val state: LiveData<State>
    val maxWordLength: Int
    val languages: LiveData<List<Language>>
    fun onInput(text: String)
    fun onSave(item: DefItem)
    fun onChooseLang(lang: Language)
    fun onRetry()
    fun onSearch(text: String)

    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Definitions(val items: List<DefItem>, val error: Boolean) : State()
        data class Completions(val items: List<String>) : State()
    }

    data class DefItem(val text: String, val saved: Boolean, val trans: List<Pair<String, Boolean>>?)
}

class AddWordImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : AddWord {

    private sealed class Action {
        data class Input(val text: String) : Action()
        data class Search(val text: String) : Action()
    }

    companion object {
        private val WORD_RANGE = 2..30
    }

    private val actions = Channel<Action>()
    private val allWords = repo.getAllWords().toStateFlow(scope)
    private val retry = Channel<Unit>()

    override val state =
        actions.receiveAsFlow()
            .distinctUntilChanged()
            .flatMapLatest {
                when (it) {
                    is Action.Input -> completionFlow(normalizeInput(it.text))
                    is Action.Search -> definitionFlow(normalizeInput(it.text))
                }
            }
            .onStart { emit(Idle) }
            .asLiveData(timeoutInMs = TimeUnit.MINUTES.toMillis(5))

    private fun normalizeInput(input: String) = input.trim().replace(Regex("\\s+"), " ")

    private fun completionFlow(input: String): Flow<State> = flow {
        if (input.length >= WORD_RANGE.first) {
            val comp = repo.getCompletions(input)
            if (comp.isNotEmpty()) {
                emit(Completions(comp))
            } else {
                emit(Completions(listOf(input)))
            }
        } else {
            emit(Completions(emptyList()))
        }
    }

    private fun defItemsFlow(input: String, loadDefResult: List<Def>?): Flow<List<DefItem>> =
        allWords
            .filterNotNull()
            .map { words ->
                val defs = if (!loadDefResult.isNullOrEmpty()) loadDefResult else listOf(Def(input, emptyList()))
                defs.map { def ->
                    val savedWord = words.find { it.text == def.text }
                    if (loadDefResult != null) {
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
            }

    private fun definitionFlow(input: String): Flow<State> =
        repo.getTargetLanguage()
            .repeatWhen(retry.receiveAsFlow())
            .transformLatest { lang ->
                if (input.length in WORD_RANGE) {
                    emit(Loading)
                    val result = try {
                        repo.getDefinitions(input, lang)
                    } catch (e: IOException) {
                        null
                    }
                    emitAll(defItemsFlow(input, result).map { Definitions(it, result == null) })
                } else {
                    emit(Idle)
                }
            }

    override val maxWordLength = WORD_RANGE.last

    override val languages: LiveData<List<Language>> =
        repo.getTargetLanguage().map {
            listOf(it) + (Language.values().toList() - it)
        }.asLiveData()

    override fun onInput(text: String) {
        actions.offer(Action.Input(text))
    }

    override fun onSave(item: DefItem) {
        scope.launch {
            if (!item.saved) {
                repo.addWord(item.text, item.trans?.map { it.first } ?: emptyList())
            } else {
                repo.addTranslations(
                    item.text, (item.trans ?: emptyList()).filter { !it.second }.map { it.first }
                )
            }
        }
    }

    override fun onChooseLang(lang: Language) {
        scope.launch {
            repo.setTargetLanguage(lang)
        }
    }

    override fun onRetry() {
        retry.offer(Unit)
    }

    override fun onSearch(text: String) {
        actions.offer(Action.Search(text))
    }
}