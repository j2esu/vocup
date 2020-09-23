package ru.uxapps.vocup.feature.learn.game

import androidx.core.view.children
import androidx.core.view.isVisible
import ru.uxapps.vocup.feature.animateVisible
import ru.uxapps.vocup.feature.learn.databinding.GameWordToTransBinding
import ru.uxapps.vocup.feature.learn.databinding.ItemAnswerBinding
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.Action.*
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.End
import ru.uxapps.vocup.feature.learn.game.WordToTranslationContract.State.Task

internal class WordToTranslationView(private val bind: GameWordToTransBinding) : GameContract.View {

    override var onAction: ((GameContract.Action) -> Unit)? = null

    private val answers: List<ItemAnswerBinding> =
        bind.gameAnswers.children.map { ItemAnswerBinding.bind(it) }.toList()

    init {
        answers.forEachIndexed { index, answerBind ->
            answerBind.answerCard.setOnClickListener {
                onAction?.invoke(Toggle(index))
            }
        }
        with(bind) {
            gameNext.setOnClickListener { onAction?.invoke(Next) }
            gamePrev.setOnClickListener { onAction?.invoke(Prev) }
            gameExamine.setOnClickListener { onAction?.invoke(Examine) }
        }
    }

    override fun render(state: GameContract.State) = with(bind) {
        when (state) {
            is Task -> {
                gameExamine.animateVisible(!state.examine)
                gameWord.isVisible = true
                gameAnswers.isVisible = true
                gameWord.text = state.word
                state.answers.forEachIndexed { index, item ->
                    val checked = state.checked.contains(index)
                    val correct = state.correct.contains(index)
                    answers[index].bind(
                        item, checked, if (state.examine && (correct || checked)) correct else null
                    )
                }
            }
            is End -> {
                gameWord.isVisible = false
                gameAnswers.isVisible = false
            }
        }
    }

    private fun ItemAnswerBinding.bind(text: String, checked: Boolean, correct: Boolean?) {
        answerText.text = text
        answerCard.isChecked = checked
        answerStatus.isSelected = correct != null
        answerStatus.isActivated = correct == true
    }
}