package ru.uxapps.vocup.screen.word

import androidx.lifecycle.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
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
    private val retryEvent = Channel<Unit>()

    override val word: LiveData<String> = MutableLiveData(wordText)
    override val translation: LiveData<TranslationFeature.State> =
        retryEvent.consumeAsFlow()
            .onStart { emit(Unit) }
            .flatMapLatest { transFeature.getTranslation(wordText) }
            .asLiveData()

    override fun onRetry() {
        retryEvent.offer(Unit)
    }
}