package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Action.*
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.State
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.State.*
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Task

internal abstract class TextToAnswersModel(
    private val scope: CoroutineScope,
    private val repo: Repo
) : GameContract.Model {

    override val state = MutableStateFlow<GameContract.State?>(null)

    private lateinit var tasks: List<Task>
    private lateinit var taskStates: MutableList<State>
    private var taskIndex = -1

    init {
        scope.launch(Dispatchers.IO) {
            tasks = loadTasks(repo)
            taskStates = tasks.map { Play(it, -1) }.toMutableList()
            next()
        }
    }

    protected abstract suspend fun loadTasks(repo: Repo): List<Task>
    protected abstract suspend fun updateProgress(task: Task, correct: Boolean, repo: Repo)

    override fun proceed(action: GameContract.Action) {
        val state = this.state.value
        when (action) {
            is Check -> if (state is Play) check(state, action.pos)
            Next -> if (state !is End) next()
            Prev -> prev()
        }
    }

    private fun check(play: Play, pos: Int) {
        scope.launch(Dispatchers.IO) {
            updateProgress(play.task, play.task.correct == play.task.answers[pos], repo)
        }
        val status = play.task.answers.mapIndexed { index, answer ->
            val checked = index == pos
            val correct = play.task.correct == answer
            if (checked || correct) correct else null
        }
        state.value = Solution(play.task, pos, status).also {
            taskStates[taskIndex] = it
        }
    }

    private fun next() {
        if (taskIndex < tasks.lastIndex) {
            taskIndex++
            state.value = taskStates[taskIndex]
        } else if (taskIndex == tasks.lastIndex) {
            val correct = taskStates.count { (it is Solution) && it.correct }
            val skipped = taskStates.count { it !is Solution }
            state.value = End(tasks.size, correct, skipped)
        }
    }

    private fun prev() {
        if (state.value is End) {
            state.value = taskStates.last()
        } else if (taskIndex > 0) {
            taskIndex--
            state.value = taskStates[taskIndex]
        }
    }
}