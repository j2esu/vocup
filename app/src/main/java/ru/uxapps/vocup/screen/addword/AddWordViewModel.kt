package ru.uxapps.vocup.screen.addword

import androidx.core.text.trimmedLength
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.repo

class AddWordViewModel : ViewModel() {

    private val transFeature = TranslationFeature(repo)
    private val wordInput = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val addWordSuccess = Channel<String>(Channel.UNLIMITED)

    val translation: LiveData<TranslationFeature.State?> =
        wordInput
            .map { it.trim() }
            .distinctUntilChanged()
            .debounce(400)
            .flatMapLatest {
                if (it.length > 1) transFeature.getTranslation(it) else flowOf(null)
            }
            .asLiveData()

    val saveEnabled: LiveData<Boolean> =
        combine(wordInput, isLoading) { input, loading ->
            input.trimmedLength() > 1 && !loading
        }.asLiveData()

    val onWordAdded: ReceiveChannel<String> = addWordSuccess

    fun onWordInput(text: String) {
        wordInput.value = text
    }

    fun onSave() {
        isLoading.value = true
        val wordText = requireNotNull(wordInput.value)
        viewModelScope.launch {
            repo.addWord(wordText)
            addWordSuccess.send(wordText)
        }
    }
}