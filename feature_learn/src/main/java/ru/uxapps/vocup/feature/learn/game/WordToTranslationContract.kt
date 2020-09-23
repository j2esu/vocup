package ru.uxapps.vocup.feature.learn.game

internal interface WordToTranslationContract {

    sealed class State : GameContract.State {
        data class Task(
            val word: String,
            val answers: List<String>,
            val correct: Set<Int>,
            val checked: Set<Int> = emptySet(),
            val examine: Boolean = false
        ) : State()
        object End : State()
    }

    sealed class Action : GameContract.Action {
        data class Toggle(val pos: Int) : Action()
        object Examine : Action()
        object Next : Action()
        object Prev : Action()
    }
}