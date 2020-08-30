package ru.uxapps.vocup.feature.addword.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Def
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.util.repeatWhen
import ru.uxapps.vocup.util.toStateFlow
import java.io.IOException
import java.util.concurrent.TimeUnit

internal class AddWordImp(
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
            .onStart { emit(AddWord.State.Idle) }
            .asLiveData(Dispatchers.IO, TimeUnit.MINUTES.toMillis(5))

    private fun normalizeInput(input: String) = input.trim().replace(Regex("\\s+"), " ")

    private fun completionFlow(input: String): Flow<AddWord.State> = flow {
        if (input.length >= WORD_RANGE.first) {
            val comp = repo.getCompletions(input)
            if (comp.isNotEmpty()) {
                emit(AddWord.State.Completions(comp))
            } else {
                emit(AddWord.State.Completions(listOf(input)))
            }
        } else {
            emit(AddWord.State.Completions(emptyList()))
        }
    }

    private fun defItemsFlow(input: String, loadDefResult: List<Def>?): Flow<List<AddWord.DefItem>> =
        allWords
            .filterNotNull()
            .map { words ->
                val defs = if (!loadDefResult.isNullOrEmpty()) loadDefResult else listOf(Def(input, emptyList()))
                defs.map { def ->
                    val savedWord = words.find { it.text == def.text }
                    if (loadDefResult != null) {
                        if (savedWord != null) {
                            AddWord.DefItem(def.text, true, def.translations.map {
                                it to savedWord.translations.contains(it)
                            })
                        } else {
                            AddWord.DefItem(def.text, false, def.translations.map { it to false })
                        }
                    } else {
                        AddWord.DefItem(def.text, savedWord != null, null)
                    }
                }
            }

    private fun definitionFlow(input: String): Flow<AddWord.State> =
        repo.getTargetLanguage()
            .repeatWhen(retry.receiveAsFlow())
            .transformLatest { lang ->
                if (input.length in WORD_RANGE) {
                    emit(AddWord.State.Loading)
                    val result = try {
                        repo.getDefinitions(input, lang)
                    } catch (e: IOException) {
                        null
                    }
                    emitAll(defItemsFlow(input, result).map { AddWord.State.Definitions(it, result == null) })
                } else {
                    emit(AddWord.State.Idle)
                }
            }

    override val maxWordLength = WORD_RANGE.last

    override val languages: LiveData<List<Language>> =
        repo.getTargetLanguage().map {
            listOf(it) + (Language.values().toList() - it)
        }.asLiveData(Dispatchers.IO)

    override fun onInput(text: String) {
        actions.offer(Action.Input(text))
    }

    override fun onSave(item: AddWord.DefItem) {
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