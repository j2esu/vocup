package ru.uxapps.vocup.feature.learn.game

internal interface WordToTranslationContract {

    sealed class State : GameContract.State {
        data class Play(val task: Task, val checked: Int) : State()
        data class Solution(val task: Task, val checked: Int, val status: List<Boolean?>) : State() {
            val correct = task.correct == task.answers[checked]
        }

        data class End(val total: Int, val correct: Int, val skipped: Int) : State() {
            val answered = total - skipped
        }
    }

    data class Task(
        val word: String,
        val answers: List<String>,
        val correct: String,
        val index: Int,
        val totalTaskCount: Int
    )

    sealed class Action : GameContract.Action {
        data class Check(val pos: Int) : Action()
        object Next : Action()
        object Prev : Action()
    }
}