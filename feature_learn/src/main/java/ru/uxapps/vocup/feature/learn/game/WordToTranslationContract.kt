package ru.uxapps.vocup.feature.learn.game

internal interface WordToTranslationContract {

    sealed class State : GameContract.State {
        data class Play(val task: Task, val checked: Set<Int>) : State()
        data class Solution(val task: Task, val checked: Set<Int>) : State()
        object End : State()
    }

    data class Task(
        val word: String,
        val answers: List<String>,
        val correct: Set<Int>,
        val taskIndex: Int,
        val taskCount: Int
    )

    sealed class Action : GameContract.Action {
        data class Toggle(val pos: Int) : Action()
        object Examine : Action()
        object Next : Action()
        object Prev : Action()
        object Finish : Action()
    }
}