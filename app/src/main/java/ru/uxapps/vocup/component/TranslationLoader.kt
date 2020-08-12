package ru.uxapps.vocup.component

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.uxapps.vocup.component.TranslationLoader.State.*
import ru.uxapps.vocup.data.Definition
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Repo

class TranslationLoader(private val repo: Repo) {

    sealed class State {
        data class Success(val result: List<Definition>) : State()
        object Fail : State()
        object Progress : State()
    }

    fun loadTranslation(word: String, lang: Language): Flow<State> =
        flow {
            emit(Progress)
            emit(Success(repo.getTranslation(word, lang)))
        }.catch { emit(Fail) }
}