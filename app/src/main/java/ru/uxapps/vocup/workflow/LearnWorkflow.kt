package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitReady
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

    override fun openGame(gameId: Int) {
        val gameFragment = GameFragment().apply { arguments = GameFragment.argsOf(gameId) }
        childFragmentManager.commit {
            replace(R.id.learn_container, gameFragment)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }
}