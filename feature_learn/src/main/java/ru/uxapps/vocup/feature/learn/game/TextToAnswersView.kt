package ru.uxapps.vocup.feature.learn.game

import android.graphics.Bitmap
import androidx.core.view.*
import androidx.transition.Transition
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialSharedAxis
import ru.uxapps.vocup.feature.back
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.learn.R
import ru.uxapps.vocup.feature.learn.databinding.GameTextToAnswersBinding
import ru.uxapps.vocup.feature.learn.databinding.ItemAnswerBinding
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Action.*
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.State.*
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract.Task
import ru.uxapps.vocup.feature.transit.ScaleVisibility

internal class TextToAnswersView(private val bind: GameTextToAnswersBinding) : GameContract.View {

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
            gameFinish.setOnClickListener { it.back() }
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
                renderTask(state.task)
                state.task.answers.forEachIndexed { index, item ->
                    answers[index].bind(item, state.checked == index, null)
                }
                taskTransition(currentTask, state.task)
            }
            is Solution -> {
                if (currentState is Play && currentTask?.index == state.task.index) {
                    TransitionManager.beginDelayedTransition(gameAnswers, ScaleVisibility())
                }
                renderTask(state.task)
                state.task.answers.forEachIndexed { index, item ->
                    answers[index].bind(item, state.checked == index, state.status[index])
                }
                taskTransition(currentTask, state.task)
            }
            is End -> {
                TransitionManager.beginDelayedTransition(
                    root, MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
                        excludeTarget(gameBottomBar, true)
                    }
                )
                gameStats.isVisible = true
                gameLevelScene.isVisible = false
                gameNext.isVisible = false
                gameNumber.isVisible = false
                gameFinish.isVisible = true
                gameStatsCorrect.text =
                    getString(R.string.game_stats_correct_pattern, state.correct, state.answered)
                gameStatsSkipped.text = getString(R.string.game_stats_skipped_pattern, state.skipped)
                gameStatsSkipped.isVisible = state.skipped > 0
            }
        }
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
                    }

                    override fun onTransitionEnd(transition: Transition) {
                        gamePrev.isEnabled = true
                        gameNext.isEnabled = true
                    }
                })
            TransitionManager.beginDelayedTransition(gameLevelScene, transit)
            gameLevelCopy.isInvisible = true
            gameLevel.isVisible = true
        }
        // save bitmap for feature transitions
        if (gameLevel.isVisible) {
            gameLevel.doOnPreDraw { levelCopy = it.drawToBitmap() }
        }
    }

    private fun renderTask(task: Task) = with(bind) {
        if (currentState is End) {
            TransitionManager.beginDelayedTransition(
                root, MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
                    excludeTarget(gameBottomBar, true)
                }
            )
        }
        gameStats.isVisible = false
        gameLevelScene.isVisible = true
        gameBottomBar.isVisible = true
        gameText.text = task.text
        gameNumber.isVisible = true
        gameNumber.text = getString(R.string.game_number_pattern, task.index + 1, task.totalTaskCount)
        gameNext.isVisible = true
        gamePrev.isVisible = task.index > 0
        gameFinish.isVisible = false
    }

    private fun ItemAnswerBinding.bind(text: String, checked: Boolean, correct: Boolean?) {
        answerText.maxLines = text.split(" ").size
        answerText.text = text
        root.isSelected = checked
        answerStatus.isActivated = correct == true
        answerStatus.isVisible = correct != null
    }
}