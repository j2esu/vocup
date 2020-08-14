package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.flow
import ru.uxapps.vocup.component.WordDetails.State.*
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.Word

interface WordDetails {

    val word: LiveData<String>
    val details: LiveData<State>

    sealed class State {
        object Loading : State()
        object Error : State()
        data class Data(val word: Word) : State()
    }
}

class WordDetailsImp(wordText: String, repo: Repo) : WordDetails {

    override val word = MutableLiveData(wordText)
    override val details = flow {
        emit(Loading)
        val word = repo.getWord(wordText)
        if (word != null) {
            emit(Data(word))
        } else {
            emit(Error)
        }
    }.asLiveData()
}