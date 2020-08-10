package ru.uxapps.vocup.screen.word

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.repo

interface WordVm {
    val word: LiveData<String>
    val translation: LiveData<TranslationFeature.State>
    fun onRetry()
}

class WordVmImp(wordText: String) : ViewModel(), WordVm {

    companion object {
        fun createFactory(wordText: String) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WordVmImp(wordText) as T
            }
        }
    }

    private val transFeature = TranslationFeature(repo)
    private val loadTransEvent = MutableStateFlow(Any())

    override val word: LiveData<String> = MutableLiveData(wordText)
    override val translation: LiveData<TranslationFeature.State> =
        loadTransEvent
            .flatMapLatest { transFeature.getTranslation(wordText) }
            .asLiveData()

    override fun onRetry() {
        loadTransEvent.value = Any()
    }
}