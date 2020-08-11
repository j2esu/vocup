package ru.uxapps.vocup.screen.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.repo

interface WordsVm {
    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
}

class WordsVmImp : ViewModel(), WordsVm {

    override val words: LiveData<List<Word>> = repo.getAllWords().asLiveData()
    override val loading: LiveData<Boolean> =
        repo.getAllWords()
            .map { it as List<Word>? }
            .onStart { emit(null) }
            .map { it == null }
            .asLiveData()

}