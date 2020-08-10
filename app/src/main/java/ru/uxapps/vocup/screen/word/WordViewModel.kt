package ru.uxapps.vocup.screen.word

import androidx.lifecycle.*
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.repo
import ru.uxapps.vocup.util.Event
import ru.uxapps.vocup.util.send

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
    private val transEvent = Event<Unit>(viewModelScope).apply { send() }

    val word: LiveData<String> = MutableLiveData(wordText)
    val translation: LiveData<TranslationFeature.State> =
        transEvent.channel.consumeAsFlow()
            .flatMapLatest { transFeature.getTranslation(wordText) }
            .asLiveData()

    fun onRetryTranslation() = transEvent.send()

}