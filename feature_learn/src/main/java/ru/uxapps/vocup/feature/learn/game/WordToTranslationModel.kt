package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Action.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Task

internal class WordToTranslationModel(
    scope: CoroutineScope,
    repo: Repo
) : GameContract.Model {

    override val state = MutableStateFlow<GameContract.State?>(null)

    private lateinit var tasks: List<Task>
    private lateinit var taskStates: MutableList<State>
    private var taskIndex = -1

    init {
        scope.launch(Dispatchers.IO) {
            val words = repo.getAllWords().first().filter { it.translations.isNotEmpty() }.shuffled()
            val translations = words.flatMap { it.translations }
            tasks = words.mapIndexed { index, word ->
                val correct = word.translations.random()
                val incorrect = translations.shuffled().filter { it != correct }.take(3)
                val answers = (incorrect + correct).shuffled()
                Task(word.text, answers, correct, index, words.size)
            }
            taskStates = tasks.map { Play(it, -1) }.toMutableList()
            next()
        }
    }

    override fun proceed(action: GameContract.Action) {
        val state = this.state.value
        when (action) {
            is Check -> if (state is Play) check(state, action.pos)
            Next -> if (state !is End) next()
            Prev -> if (state !is End) prev()
            Finish -> if (state !is End) finish()
        }
    }

    private fun check(play: Play, pos: Int) {
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
        if (taskIndex < tasks.size - 1) {
            taskIndex++
            state.value = taskStates[taskIndex]
        }
    }

    private fun prev() {
        if (taskIndex > 0) {
            taskIndex--
            state.value = taskStates[taskIndex]
        }
    }

    private fun finish() {
        state.value = End
    }
}