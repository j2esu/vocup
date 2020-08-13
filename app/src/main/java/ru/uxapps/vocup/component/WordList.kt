package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.Word

interface WordList {
    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
}

class WordListImp(repo: Repo) : WordList {

    override val words: LiveData<List<Word>> = repo.getAllWords().asLiveData()
    override val loading: LiveData<Boolean> =
        repo.getAllWords()
            .map { it as List<Word>? }
            .onStart { emit(null) }
            .map { it == null }
            .asLiveData()
}