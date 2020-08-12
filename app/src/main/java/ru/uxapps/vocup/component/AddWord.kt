package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.component.AddWord.Translation
import ru.uxapps.vocup.data.Definition
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.util.LiveEvent
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send
import java.io.IOException

interface AddWord {

    val translation: LiveData<Translation>
    val saveEnabled: LiveData<Boolean>
    val maxWordLength: Int
    val languages: LiveData<List<Language>>
    val onWordAdded: LiveEvent<String>
    fun onWordInput(text: String)
    fun onSave()
    fun chooseLang(lang: Language)

    sealed class Translation {
        object Idle : Translation()
        object Progress : Translation()
        data class Success(val result: List<Definition>) : Translation()
        object Fail : Translation()
    }
}

class AddWordImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : AddWord {

    companion object {
        private val WORD_RANGE = 2..30
    }

    private val wordInput = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)

    override val translation: LiveData<Translation> =
        wordInput
            .map { normalizeInput(it) }
            .distinctUntilChanged()
            .combine(repo.getTargetLang()) { input, lang -> input to lang }
            .transformLatest { (input, lang) ->
                if (input.length in WORD_RANGE) {
                    emit(Translation.Progress)
                    delay(400)
                    val result = try {
                        repo.getTranslation(input, lang)
                    } catch (e: IOException) {
                        null
                    }
                    emit(if (result != null) Translation.Success(result) else Translation.Fail)
                } else {
                    emit(Translation.Idle)
                }
            }
            .asLiveData()

    private fun normalizeInput(input: String) =
        input.trim().replace(Regex("\\s+"), "")

    override val saveEnabled: LiveData<Boolean> =
        combine(wordInput, isLoading) { input, loading ->
            normalizeInput(input).length in WORD_RANGE && !loading
        }.asLiveData()

    override val maxWordLength = WORD_RANGE.last

    override val languages: LiveData<List<Language>> =
        repo.getTargetLang().map {
            listOf(it) + (Language.values().toList() - it)
        }.asLiveData()

    override val onWordAdded = MutableLiveEvent<String>()

    override fun onWordInput(text: String) {
        wordInput.value = text
    }

    override fun onSave() {
        isLoading.value = true
        val wordText = requireNotNull(wordInput.value)
        scope.launch {
            repo.addWord(wordText)
            onWordAdded.send(wordText)
        }
    }

    override fun chooseLang(lang: Language) {
        scope.launch {
            repo.setTargetLang(lang)
        }
    }
}