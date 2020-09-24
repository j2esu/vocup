package ru.uxapps.vocup.feature.learn.game

import androidx.core.view.children
import androidx.core.view.isVisible
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.learn.R
import ru.uxapps.vocup.feature.learn.databinding.GameWordToTransBinding
import ru.uxapps.vocup.feature.learn.databinding.ItemAnswerBinding
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Action.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Task

internal class WordToTranslationView(private val bind: GameWordToTransBinding) : GameContract.View {

    override var onAction: ((GameContract.Action) -> Unit)? = null

    private val answers: List<ItemAnswerBinding> =
        bind.gameAnswers.children.map { ItemAnswerBinding.bind(it) }.toList()

    init {
        answers.forEachIndexed { index, answerBind ->
            answerBind.answerCard.setOnClickListener {
                onAction?.invoke(Check(index))
            }
        }
        with(bind) {
            gameNext.setOnClickListener { onAction?.invoke(Next) }
            gamePrev.setOnClickListener { onAction?.invoke(Prev) }
            gameFinish.setOnClickListener { onAction?.invoke(Finish) }
        }
    }

    override fun render(state: GameContract.State) = with(bind) {
        when (state) {
            is Play -> {
                bindTask(state.task)
                state.task.answers.forEachIndexed { index, item ->
                    answers[index].bind(item, state.checked == index, null)
                }
            }
            is Solution -> {
                bindTask(state.task)
                state.task.answers.forEachIndexed { index, item ->
                    answers[index].bind(item, state.checked == index, state.status[index])
                }
            }
            is End -> root.isVisible = false
        }
    }

    private fun bindTask(task: Task) = with(bind) {
        root.isVisible = true
        gameWord.text = task.word
        gameNumber.text = getString(R.string.game_number_pattern, task.taskIndex + 1, task.totalTaskCount)
        gameNext.isVisible = task.taskIndex < task.totalTaskCount - 1
        gameFinish.isVisible = task.taskIndex == task.totalTaskCount - 1
        gamePrev.isVisible = task.taskIndex > 0
    }

    private fun ItemAnswerBinding.bind(text: String, checked: Boolean, correct: Boolean?) {
        answerText.maxLines = text.split(" ").size
        answerText.text = text
        answerCard.isChecked = checked
        answerCard.isSelected = correct != null
        answerCard.isActivated = correct == true
    }
}