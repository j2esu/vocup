package ru.uxapps.vocup.screen.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.repo

interface WordsVm {
    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
}

class WordsVmImp : ViewModel(), WordsVm {

    override val words: LiveData<List<Word>> = repo.getAllWords().filterNotNull().asLiveData()
    override val loading: LiveData<Boolean> = repo.getAllWords().map { it == null }.asLiveData()

}