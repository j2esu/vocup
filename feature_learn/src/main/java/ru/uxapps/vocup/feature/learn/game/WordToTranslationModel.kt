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
                Task(word.text, answers, setOf(answers.indexOf(correct)), index, words.size)
            }
            taskStates = tasks.map { Play(it, emptySet()) }.toMutableList()
            next()
        }
    }

    override fun proceed(action: GameContract.Action) {
        val state = this.state.value
        when (action) {
            is Toggle -> if (state is Play) toggleAnswer(state, action.pos)
            Examine -> if (state is Play) examine(state)
            Next -> if (state !is End) next()
            Prev -> if (state !is End) prev()
            Finish -> if (state !is End) finish()
        }
    }

    private fun toggleAnswer(play: Play, pos: Int) {
        state.value = play.copy(checked = play.checked.toMutableSet().apply {
            if (!add(pos)) remove(pos)
        }).also {
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

    private fun examine(play: Play) {
        state.value = Solution(play.task, play.checked).also {
            taskStates[taskIndex] = it
        }
    }

    private fun finish() {
        state.value = End
    }
}