package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Task

internal class TransToWordModel(
    scope: CoroutineScope,
    repo: Repo
) : TextToAnswersModel(scope, repo) {

    private lateinit var taskWords: List<Word>

    override suspend fun loadTasks(repo: Repo): List<Task> {
        val words = repo.getAllWords().first()
        val trans = words.flatMap { it.translations }
        val transToWord = words
            .flatMap { word -> word.translations.map { it to word } }
            .filter { pair -> trans.count { it == pair.first } == 1 }
            .shuffled()
        val wordsText = words.map { it.text }
        taskWords = transToWord.map { it.second }
        return transToWord.mapIndexed { index, (trans, word) ->
            val correct = word.text
            val incorrect = (wordsText - correct).shuffled().take(3)
            val answers = (incorrect + correct).shuffled()
            Task(trans, answers, correct, index, transToWord.size)
        }
    }

    override suspend fun updateProgress(task: Task, correct: Boolean, repo: Repo) {
        repo.updateProgress(taskWords[task.index], if (correct) 5 else -1)
    }
}
