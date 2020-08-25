package ru.uxapps.vocup

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.databinding.FragmentNavBinding
import ru.uxapps.vocup.feature.explore.ExploreFragment
import ru.uxapps.vocup.feature.learn.LearnFragment
import ru.uxapps.vocup.util.host
import ru.uxapps.vocup.workflow.DictWorkflow

class NavFragment : Fragment(R.layout.fragment_nav), DictWorkflow.Router {

    interface Router {
        fun openAddWord()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.nav_container, DictWorkflow().also { setPrimaryNavigationFragment(it) })
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val bind = FragmentNavBinding.bind(requireView())
        bind.navPager.apply {
            setOnNavigationItemSelectedListener {
                if (selectedItemId != it.itemId) {
                    val fragment = when (it.itemId) {
                        R.id.menu_nav_dict -> DictWorkflow()
                        R.id.menu_nav_learn -> LearnFragment()
                        R.id.menu_nav_explore -> ExploreFragment()
                        else -> error("Unknown menu: ${it.title}")
                    }
                    childFragmentManager.commit {
                        replace(R.id.nav_container, fragment)
                        setPrimaryNavigationFragment(fragment)
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    }
                } else {
                    childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                true
            }
        }
    }

    override fun openAddWord() = host<Router>().openAddWord()
}