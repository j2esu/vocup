package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Task

internal class TransToWordModel(
    scope: CoroutineScope,
    repo: Repo
) : TextToAnswersModel(scope, repo) {

    override suspend fun loadTasks(repo: Repo): List<Task> {
        val words = repo.getAllWords().first()
        val trans = words.flatMap { it.translations }
        val transToWord = words
            .flatMap { word -> word.translations.map { it to word.text } }
            .filter { pair -> trans.count { it == pair.first } == 1 }
            .shuffled()
        val wordsText = words.map { it.text }
        return transToWord.mapIndexed { index, (trans, word) ->
            val incorrect = (wordsText - word).shuffled().take(3)
            val answers = (incorrect + word).shuffled()
            Task(trans, answers, word, index, transToWord.size)
        }
    }
}