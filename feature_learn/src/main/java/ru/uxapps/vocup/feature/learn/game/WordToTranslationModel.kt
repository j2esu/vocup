package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Action.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.AnswerItem
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State

internal class WordToTranslationModel(
    scope: CoroutineScope,
    repo: Repo
) : GameContract.Model {

    override val state = MutableStateFlow<GameContract.State?>(null)

    private lateinit var words: List<Word>

    private lateinit var translations: List<String>
    private var taskIndex = -1

    init {
        // load game data
        scope.launch(Dispatchers.IO) {
            words = repo.getAllWords().first().filter { it.translations.isNotEmpty() }.shuffled()
            translations = words.flatMap { it.translations }
            nextTask()
        }
    }

    override fun proceed(action: GameContract.Action) {
        when (action) {
            is Toggle -> toggleAnswer(action.pos)
            Examine -> examine()
            Next -> nextTask()
            Prev -> prevTask()
        }
    }

    private fun toggleAnswer(pos: Int) {
        val task = state.value as State.Task
        state.value = task.copy(answers = task.answers.toMutableList().apply {
            val clicked = get(pos)
            set(pos, clicked.copy(checked = !clicked.checked))
        })
    }

    private fun nextTask() {
        taskIndex++
        if (taskIndex >= words.size) {
            state.value = State.End
        } else {
            val word = words[taskIndex]
            val correctTrans = word.translations.random()
            val incorrectTrans = translations.shuffled().filter { it != correctTrans }.take(3)
            val answers = (incorrectTrans + correctTrans).shuffled().map {
                AnswerItem(it, false)
            }
            state.value = State.Task(word.text, answers)
        }
    }

    private fun prevTask() {

    }

    private fun examine() {

    }
}