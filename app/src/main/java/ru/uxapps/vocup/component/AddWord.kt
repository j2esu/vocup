package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.component.AddWord.TransItem
import ru.uxapps.vocup.component.AddWord.TransState
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.Trans
import ru.uxapps.vocup.util.asStateFlow
import java.io.IOException

interface AddWord {

    val translation: LiveData<TransState>
    val maxWordLength: Int
    val languages: LiveData<List<Language>>
    fun onInput(text: String)
    fun onSave(item: TransItem)
    fun onRemove(item: TransItem)
    fun onChooseLang(lang: Language)

    sealed class TransState {
        object Idle : TransState()
        object Progress : TransState()
        data class Success(val result: List<TransItem>) : TransState()
        object Fail : TransState()
    }

    data class TransItem(val trans: Trans, val saved: Boolean)
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

    override val translation: LiveData<TransState> =
        wordInput
            .map { normalizeInput(it) }
            .distinctUntilChanged()
            .combine(repo.getTargetLang()) { input, lang -> input to lang }
            .transformLatest { (input, lang) ->
                if (input.length in WORD_RANGE) {
                    emit(TransState.Progress)
                    delay(400)
                    val result = try {
                        repo.getTranslation(input, lang)
                    } catch (e: IOException) {
                        null
                    }
                    if (result != null) {
                        emitAll(allWords.filterNotNull().map { savedWords ->
                            TransState.Success(result.map { trans ->
                                TransItem(trans, savedWords.any { it.text == trans.text })
                            })
                        })
                    } else {
                        emit(TransState.Fail)
                    }
                } else {
                    emit(TransState.Idle)
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

    override fun onSave(item: TransItem) {
        scope.launch {
            repo.addWord(item.trans.text)
        }
    }

    override fun onRemove(item: TransItem) {
        scope.launch {
            repo.removeWord(item.trans.text)
        }
    }

    override fun onChooseLang(lang: Language) {
        scope.launch {
            repo.setTargetLang(lang)
        }
    }
}