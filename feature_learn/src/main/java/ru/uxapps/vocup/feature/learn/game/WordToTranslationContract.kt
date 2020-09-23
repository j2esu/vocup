package ru.uxapps.vocup.feature.learn.game

internal interface WordToTranslationContract {

    sealed class State : GameContract.State {
        data class Task(val word: String, val answers: List<AnswerItem>) : State()
        object End : State()
    }

    data class AnswerItem(
        val trans: String,
        val checked: Boolean
    )

    sealed class Action : GameContract.Action {
        data class Toggle(val pos: Int) : Action()
        object Examine : Action()
        object Next : Action()
        object Prev : Action()
    }
}