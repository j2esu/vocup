package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.commit
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitReady
import ru.uxapps.vocup.feature.getColorAttr
import ru.uxapps.vocup.feature.learn.GameFragment
import ru.uxapps.vocup.feature.learn.LearnFragment

class LearnWorkflow : BaseFragment(R.layout.workflow_learn), LearnFragment.Host {

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            val learnFragment = LearnFragment()
            childFragmentManager.commit {
                add(R.id.learn_container, learnFragment)
                setReorderingAllowed(true)
            }
            postponeUntil {
                learnFragment.awaitReady()
            }
        }
    }

    override fun openGame(gameId: Int, srcView: View) {
        // exit
        val learnFragment = childFragmentManager.findFragmentById(R.id.learn_container) as LearnFragment
        learnFragment.exitTransition = MaterialElevationScale(false)
        learnFragment.postpone()
        // enter
        val gameFragment = GameFragment().apply { arguments = GameFragment.argsOf(gameId) }
        gameFragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.learn_container
            setAllContainerColors(requireContext().getColorAttr(android.R.attr.colorBackground))
        }
        // transaction
        childFragmentManager.commit {
            replace(R.id.learn_container, gameFragment)
            addSharedElement(srcView, getString(R.string.transit_game_root))
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}