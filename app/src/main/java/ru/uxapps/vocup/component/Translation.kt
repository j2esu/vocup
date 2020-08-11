package ru.uxapps.vocup.component

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.uxapps.vocup.component.Translation.State.*
import ru.uxapps.vocup.data.Repo
import java.io.IOException

class Translation(private val repo: Repo) {

    sealed class State {
        data class Success(val result: String) : State()
        object Fail : State()
        object Progress : State()
    }

    fun getTranslation(text: String): Flow<State> = flow {
        emit(Progress)
        val trans = try {
            repo.getTranslation(text)
        } catch (e: IOException) {
            null
        }
        emit(if (trans != null) Success(trans) else Fail)
    }
}