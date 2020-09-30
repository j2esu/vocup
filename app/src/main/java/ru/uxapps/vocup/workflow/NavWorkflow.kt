package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.transition.TransitionSet
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialFadeThrough
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.api.Kit
import ru.uxapps.vocup.databinding.WorkflowNavBinding
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.ScaleVisibility
import ru.uxapps.vocup.feature.awaitReady
import ru.uxapps.vocup.feature.explore.ExploreFragment
import ru.uxapps.vocup.util.host

class NavWorkflow : BaseFragment(R.layout.workflow_nav), DictWorkflow.Router, ExploreFragment.Router {

    interface Router {
        fun openAddWord(srcView: View)
        fun openAddWordList(title: String, list: List<String>, srcView: View)
    }

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            val dictWorkflow = DictWorkflow()
            childFragmentManager.commit {
                add(R.id.nav_container, dictWorkflow)
                setPrimaryNavigationFragment(dictWorkflow)
                setReorderingAllowed(true)
            }
            postponeUntil {
                dictWorkflow.awaitReady()
            }
        }
        val bind = WorkflowNavBinding.bind(view)
        bind.navBar.apply {
            setOnNavigationItemSelectedListener {
                if (selectedItemId != it.itemId) {
                    val changeNavTransitProvider = {
                        TransitionSet()
                            .addTransition(MaterialFadeThrough().apply {
                                excludeTarget(AppBarLayout::class.java, true)
                                excludeTarget(FloatingActionButton::class.java, true)
                            })
                            .addTransition(ScaleVisibility().apply {
                                addTarget(FloatingActionButton::class.java)
                            })
                    }
                    // exit
                    val currentFragment = childFragmentManager.findFragmentById(R.id.nav_container) as BaseFragment
                    currentFragment.exitTransition = changeNavTransitProvider()
                    // enter
                    val newFragment = when (it.itemId) {
                        R.id.menu_nav_dict -> DictWorkflow()
                        R.id.menu_nav_learn -> LearnWorkflow()
                        R.id.menu_nav_explore -> ExploreFragment()
                        else -> error("Unknown menu: ${it.title}")
                    }
                    newFragment.enterTransition = changeNavTransitProvider()
                    // transaction
                    childFragmentManager.commit {
                        replace(R.id.nav_container, newFragment)
                        setPrimaryNavigationFragment(newFragment)
                        setReorderingAllowed(true)
                    }
                } else {
                    childFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                true
            }
        }
    }

    override fun openAddWord(srcView: View) = host<Router>().openAddWord(srcView)

    override fun openAddKit(kit: Kit, srcView: View) =
        host<Router>().openAddWordList(kit.title, kit.words, srcView)
}