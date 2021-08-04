package ru.uxapps.vocup.feature.learn.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import ru.uxapps.vocup.feature.learn.databinding.FragmentGameBinding
import ru.uxapps.vocup.feature.learn.databinding.GameTextToAnswersBinding
import ru.uxapps.vocup.feature.learn.game.GameContract
import ru.uxapps.vocup.feature.learn.game.TextToAnswersContract
import ru.uxapps.vocup.feature.learn.game.TextToAnswersView
import ru.uxapps.vocup.feature.setNavAsBack

internal class GameView(
    private val bind: FragmentGameBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onAction(action: GameContract.Action)
    }

    private var viewProvider: ViewProvider? = null
    private var view: GameContract.View? = null

    init {
        bind.gameToolbar.setNavAsBack()
    }

    fun setState(state: GameContract.State?) = with(bind) {
        gameContainer.isVisible = state != null
        gameProgress.isVisible = state == null
        if (state != null) {
            val provider = VIEW_PROVIDERS.first { it.accept(state) }
            if (viewProvider != provider) {
                viewProvider = provider
                view?.onAction = null
                gameContainer.removeAllViews()
                view = provider.attach(gameContainer).apply {
                    onAction = { callback.onAction(it) }
                }
            }
            view?.render(state)
        }
    }
}

private interface ViewProvider {
    fun accept(state: GameContract.State): Boolean
    fun attach(parent: ViewGroup): GameContract.View
}

private val VIEW_PROVIDERS = listOf<ViewProvider>(
    object : ViewProvider {
        override fun accept(state: GameContract.State) = state is TextToAnswersContract.State
        override fun attach(parent: ViewGroup) = TextToAnswersView(
            GameTextToAnswersBinding.inflate(LayoutInflater.from(parent.context), parent, true)
        )
    }
)
