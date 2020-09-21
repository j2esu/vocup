package ru.uxapps.vocup.feature.learn

import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitValue
import ru.uxapps.vocup.feature.learn.databinding.FragmentLearnBinding
import ru.uxapps.vocup.feature.learn.di.LearnViewModel
import ru.uxapps.vocup.feature.learn.model.GameItem
import ru.uxapps.vocup.feature.learn.model.GameListModel
import ru.uxapps.vocup.feature.learn.view.LearnView
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class LearnFragment : BaseFragment(R.layout.fragment_learn) {

    interface Host {
        fun openGame(gameId: Int)
    }

    private val vm by viewModels<LearnViewModel>()

    @Inject internal lateinit var gameListModel: GameListModel

    override fun onViewReady(view: View, init: Boolean) {
        vm.learnComponent.inject(this)
        val learnView = LearnView(FragmentLearnBinding.bind(view), object : LearnView.Callback {
            override fun onStart(item: GameItem) = host<Host>().openGame(item.game.ordinal)

            override fun onPlay() =
                Toast.makeText(context, "Start random game", Toast.LENGTH_SHORT).show()
        })
        gameListModel.games.observe(viewLifecycleOwner, learnView::setGames)
        gameListModel.playEnabled.observe(viewLifecycleOwner, learnView::setPlayEnabled)
        postponeUntil {
            gameListModel.playEnabled.awaitValue()
        }
    }
}