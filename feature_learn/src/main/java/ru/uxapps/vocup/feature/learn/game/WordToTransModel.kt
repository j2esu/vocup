package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Task

internal class WordToTransModel(
    scope: CoroutineScope,
    repo: Repo
) : TextToAnswersModel(scope, repo) {

    override suspend fun loadTasks(repo: Repo): List<Task> {
        val words = repo.getAllWords().first().filter { it.translations.isNotEmpty() }.shuffled()
        val translations = words.flatMap { it.translations }.distinct()
        return words.mapIndexed { index, word ->
            val correct = word.translations.random()
            val incorrect = (translations - word.translations).shuffled().take(3)
            val answers = (incorrect + correct).shuffled()
            Task(word.text, answers, correct, index, words.size)
        }
    }
}