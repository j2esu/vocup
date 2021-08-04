package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Task

internal class WordToTransModel(
    scope: CoroutineScope,
    repo: Repo
) : TextToAnswersModel(scope, repo) {

    private lateinit var taskWords: List<Word>

    override suspend fun loadTasks(repo: Repo): List<Task> {
        val words = repo.getAllWords().first().filter { it.translations.isNotEmpty() }.shuffled()
        val translations = words.flatMap { it.translations }.distinct()
        taskWords = words
        return words.mapIndexed { index, word ->
            val correct = word.translations.random()
            val incorrect = (translations - word.translations).shuffled().take(3)
            val answers = (incorrect + correct).shuffled()
            Task(word.text, answers, correct, index, words.size)
        }
    }

    override suspend fun updateProgress(task: Task, correct: Boolean, repo: Repo) {
        repo.updateProgress(taskWords[task.index], if (correct) 5 else -1)
    }
}
