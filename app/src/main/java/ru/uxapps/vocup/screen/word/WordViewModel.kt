package ru.uxapps.vocup.screen.word

import androidx.lifecycle.*
import kotlinx.coroutines.flow.flatMapLatest
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.repo

class WordViewModel(wordText: String) : ViewModel() {

    companion object {
        fun createFactory(wordText: String) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return WordViewModel(wordText) as T
            }
        }
    }

    private val transFeature = TranslationFeature(repo)
    private val transEvent = MutableLiveData(Unit)

    val word: LiveData<String> = MutableLiveData(wordText)
    val translation: LiveData<TranslationFeature.State> =
        transEvent.asFlow()
            .flatMapLatest { transFeature.getTranslation(wordText) }
            .asLiveData()

    fun onRetry() {
        transEvent.value = Unit
    }
}