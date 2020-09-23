package ru.uxapps.vocup.feature.learn

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.learn.databinding.FragmentGameBinding
import ru.uxapps.vocup.feature.learn.di.GameViewModel
import ru.uxapps.vocup.feature.learn.game.GameContract
import ru.uxapps.vocup.feature.learn.model.GameModel
import ru.uxapps.vocup.feature.learn.view.GameView
import javax.inject.Inject

class GameFragment : BaseFragment(R.layout.fragment_game) {

    companion object {
        fun argsOf(gameId: Int) = bundleOf("game" to gameId)
        private val GameFragment.gameId: Int get() = requireArguments()["game"] as Int
    }

    private val vm by viewModels<GameViewModel>()

    @Inject internal lateinit var game: GameModel

    override fun onViewReady(view: View, init: Boolean) {
        vm.getGameComponent(gameId).inject(this)
        val gameView = GameView(FragmentGameBinding.bind(view), object : GameView.Callback {
            override fun onAction(action: GameContract.Action) = game.onAction(action)
        })
        game.state.observe(viewLifecycleOwner, gameView::setState)
    }
}