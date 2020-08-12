package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.flow
import ru.uxapps.vocup.data.Repo

interface WordDetails {
    val word: LiveData<String>
    val details: LiveData<String?>
}

class WordDetailsImp(wordText: String, repo: Repo) : WordDetails {

    override val word: LiveData<String> = MutableLiveData(wordText)
    override val details: LiveData<String?> = flow {
        emit(null)
        emit(repo.getDetails(wordText))
    }.asLiveData()
}