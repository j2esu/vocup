package ru.uxapps.vocup.screen.addword

import androidx.core.text.trimmedLength
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.repo
import ru.uxapps.vocup.util.LiveEvent
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

interface AddWordVm {
    val translation: LiveData<TranslationFeature.State?>
    val saveEnabled: LiveData<Boolean>
    val languages: LiveData<List<Language>>
    val onWordAdded: LiveEvent<String>
    fun onWordInput(text: String)
    fun onSave()
    fun chooseLang(lang: Language)
}

class AddWordVmImp : ViewModel(), AddWordVm {

    private val transFeature = TranslationFeature(repo)
    private val wordInput = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)

    override val translation: LiveData<TranslationFeature.State?> =
        wordInput
            .map { it.trim() }
            .distinctUntilChanged()
            .debounce(400)
            .flatMapLatest {
                if (it.length > 1) transFeature.getTranslation(it) else flowOf(null)
            }
            .asLiveData()

    override val saveEnabled: LiveData<Boolean> =
        combine(wordInput, isLoading) { input, loading ->
            input.trimmedLength() > 1 && !loading
        }.asLiveData()

    override val languages: LiveData<List<Language>> =
        repo.getTargetLang().map { listOf(it) + Language.values() }.asLiveData()


    override val onWordAdded = MutableLiveEvent<String>()

    override fun onWordInput(text: String) {
        wordInput.value = text
    }

    override fun onSave() {
        isLoading.value = true
        val wordText = requireNotNull(wordInput.value)
        viewModelScope.launch {
            repo.addWord(wordText)
            onWordAdded.send(wordText)
        }
    }

    override fun chooseLang(lang: Language) {
        TODO("not implemented")
    }
}