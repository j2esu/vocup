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
import ru.uxapps.vocup.feature.learn.model.Games
import ru.uxapps.vocup.feature.learn.view.LearnView
import javax.inject.Inject

class LearnFragment : BaseFragment(R.layout.fragment_learn) {

    private val vm by viewModels<LearnViewModel>()

    @Inject internal lateinit var gamesModel: Games

    override fun onViewReady(view: View, init: Boolean) {
        vm.learnComponent.inject(this)
        val learnView = LearnView(FragmentLearnBinding.bind(view), object : LearnView.Callback {
            override fun onStart(item: GameItem) =
                Toast.makeText(context, "Open ${getString(item.game.title)}", Toast.LENGTH_SHORT).show()

            override fun onPlay() =
                Toast.makeText(context, "Start random game", Toast.LENGTH_SHORT).show()
        })
        gamesModel.games.observe(viewLifecycleOwner, learnView::setGames)
        gamesModel.playEnabled.observe(viewLifecycleOwner, learnView::setPlayEnabled)
        postponeUntil {
            gamesModel.playEnabled.awaitValue()
        }
    }
}