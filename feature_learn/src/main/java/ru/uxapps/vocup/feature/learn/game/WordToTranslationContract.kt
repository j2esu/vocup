package ru.uxapps.vocup.feature.learn.game

internal interface WordToTranslationContract {

    sealed class State : GameContract.State {
        data class Task(val word: String, val answers: List<String>, val checked: Set<Int>) : State()
        object End : State()
    }

    sealed class Action : GameContract.Action {
        data class Toggle(val pos: Int) : Action()
        object Examine : Action()
        object Next : Action()
        object Prev : Action()
    }
}