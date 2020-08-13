package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.flow
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.Word

interface WordDetails {
    val word: LiveData<String>
    val details: LiveData<Word?>
}

class WordDetailsImp(wordText: String, repo: Repo) : WordDetails {

    override val word = MutableLiveData(wordText)
    override val details = flow {
        emit(null)
        emit(repo.getWord(wordText))
    }.asLiveData()
}