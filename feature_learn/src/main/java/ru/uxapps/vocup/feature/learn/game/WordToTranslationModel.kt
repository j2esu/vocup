package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Action.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.End
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.Task

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
        val task = state.value as Task
        state.value = task.copy(checked = task.checked.toMutableSet().apply {
            if (!add(pos)) remove(pos)
        })
    }

    private fun nextTask() {
        taskIndex++
        if (taskIndex >= words.size) {
            state.value = End
        } else {
            val word = words[taskIndex]
            val correctTrans = word.translations.random()
            val incorrectTrans = translations.shuffled().filter { it != correctTrans }.take(3)
            val answers = (incorrectTrans + correctTrans).shuffled()
            state.value = Task(word.text, answers, emptySet())
        }
    }

    private fun prevTask() {

    }

    private fun examine() {

    }
}