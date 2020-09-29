package ru.uxapps.vocup.feature.learn

import android.view.View
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
        fun openGame(gameId: Int, srcView: View)
    }

    private val vm by viewModels<LearnViewModel>()

    @Inject internal lateinit var gameListModel: GameListModel

    override fun onViewReady(view: View, init: Boolean) {
        vm.learnComponent.inject(this)
        val learnView = LearnView(FragmentLearnBinding.bind(view), object : LearnView.Callback {
            override fun onStart(item: GameItem, srcView: View) = host<Host>().openGame(item.game.id, srcView)
        })
        gameListModel.games.observe(viewLifecycleOwner, learnView::setGames)
        postponeUntil {
            gameListModel.games.awaitValue()
        }
    }
}