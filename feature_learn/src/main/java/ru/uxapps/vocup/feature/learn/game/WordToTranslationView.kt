package ru.uxapps.vocup.feature.learn.game

import android.graphics.Bitmap
import androidx.core.view.*
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialSharedAxis
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.learn.R
import ru.uxapps.vocup.feature.learn.databinding.GameWordToTransBinding
import ru.uxapps.vocup.feature.learn.databinding.ItemAnswerBinding
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Action.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Task
import ru.uxapps.vocup.feature.transit.ScaleVisibility

internal class WordToTranslationView(private val bind: GameWordToTransBinding) : GameContract.View {

    override var onAction: ((GameContract.Action) -> Unit)? = null

    private val answers: List<ItemAnswerBinding> =
        bind.gameAnswers.children.map { ItemAnswerBinding.bind(it) }.toList()

    private var currentState: GameContract.State? = null

    init {
        answers.forEachIndexed { index, answerBind ->
            answerBind.root.setOnClickListener {
                onAction?.invoke(Check(index))
            }
        }
        with(bind) {
            gameNext.setOnClickListener { onAction?.invoke(Next) }
            gamePrev.setOnClickListener { onAction?.invoke(Prev) }
            gameFinish.setOnClickListener { onAction?.invoke(Finish) }
        }
    }

    private var levelCopy: Bitmap? = null

    override fun render(state: GameContract.State) = with(bind) {
        val currentTask = getTask(currentState)
        // bottom bar transition
        TransitionManager.beginDelayedTransition(gameBottomBar)
        // render state
        when (state) {
            is Play -> {
                bindTask(state.task)
                state.task.answers.forEachIndexed { index, item ->
                    answers[index].bind(item, state.checked == index, null)
                }
                taskTransition(currentTask, state.task)
            }
            is Solution -> {
                if (currentState is Play && currentTask?.index == state.task.index) {
                    TransitionManager.beginDelayedTransition(gameAnswers, ScaleVisibility())
                }
                bindTask(state.task)
                state.task.answers.forEachIndexed { index, item ->
                    answers[index].bind(item, state.checked == index, state.status[index])
                }
                taskTransition(currentTask, state.task)
            }
            is End -> root.isVisible = false
        }
        // save bitmap for feature transitions
        gameLevel.doOnPreDraw { levelCopy = it.drawToBitmap() }
        // update current state
        currentState = state
    }

    private fun getTask(state: GameContract.State?): Task? {
        return when (state) {
            is Play -> state.task
            is Solution -> state.task
            else -> null
        }
    }

    private fun taskTransition(from: Task?, to: Task) = with(bind) {
        if (from != null && from.index != to.index) {
            gameLevelCopy.setImageBitmap(levelCopy)
            gameLevelCopy.isVisible = true
            gameLevel.isInvisible = true
            val transit = MaterialSharedAxis(MaterialSharedAxis.X, from.index < to.index).addListener(
                object : TransitionListenerAdapter() {
                    override fun onTransitionStart(transition: Transition) {
                        gamePrev.isEnabled = false
                        gameNext.isEnabled = false
                        gameFinish.isEnabled = false
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        gamePrev.isEnabled = true
                        gameNext.isEnabled = true
                        gameFinish.isEnabled = true
                    }
                })
            TransitionManager.beginDelayedTransition(levelScene, transit)
            gameLevelCopy.isInvisible = true
            gameLevel.isVisible = true
        }
    }

    private fun bindTask(task: Task) = with(bind) {
        root.isVisible = true
        gameWord.text = task.word
        gameNumber.text = getString(R.string.game_number_pattern, task.index + 1, task.totalTaskCount)
        gameNext.isVisible = task.index < task.totalTaskCount - 1
        gameFinish.isVisible = task.index == task.totalTaskCount - 1
        gamePrev.isVisible = task.index > 0
    }

    private fun ItemAnswerBinding.bind(text: String, checked: Boolean, correct: Boolean?) {
        answerText.maxLines = text.split(" ").size
        answerText.text = text
        root.isSelected = checked
        answerStatus.isActivated = correct == true
        answerStatus.isVisible = correct != null
    }
}