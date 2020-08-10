package ru.uxapps.vocup.screen.words

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.repo

class WordsViewModel : ViewModel() {

    val words: LiveData<List<Word>> = repo.getAllWords().filterNotNull().asLiveData()
    val loading :LiveData<Boolean> = repo.getAllWords().map { it == null }.asLiveData()

}