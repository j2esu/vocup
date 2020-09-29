package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.transition.TransitionSet
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitReady
import ru.uxapps.vocup.feature.dictionary.DictFragment
import ru.uxapps.vocup.feature.getColorAttr
import ru.uxapps.vocup.feature.worddetails.WordFragment
import ru.uxapps.vocup.feature.ScaleVisibility
import ru.uxapps.vocup.util.host

class DictWorkflow : BaseFragment(R.layout.workflow_dict), DictFragment.Router, WordFragment.Router {

    interface Router {
        fun openAddWord(srcView: View)
    }

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            val dictFragment = DictFragment()
            childFragmentManager.commit {
                add(R.id.dict_container, dictFragment)
                setReorderingAllowed(true)
            }
            postponeUntil {
                dictFragment.awaitReady()
            }
        }
    }

    override fun openWord(wordId: Long, srcView: View) {
        // setup exit
        val dictFrag = childFragmentManager.findFragmentById(R.id.dict_container) as DictFragment
        dictFrag.exitTransition = TransitionSet()
            .addTransition(MaterialElevationScale(false).apply {
                duration = 400
                excludeTarget(getString(R.string.transit_dict_add), true)
            })
            .addTransition(ScaleVisibility().apply {
                duration = 100
                addTarget(getString(R.string.transit_dict_add))
            })
        dictFrag.reenterTransition = TransitionSet()
            .addTransition(MaterialElevationScale(true).apply {
                duration = 400
                excludeTarget(getString(R.string.transit_dict_add), true)
            })
            .addTransition(ScaleVisibility().apply {
                startDelay = 200
                duration = 200
                addTarget(getString(R.string.transit_dict_add))
            })
        dictFrag.postpone()
        // setup enter
        val wordFrag = WordFragment().apply { arguments = WordFragment.argsOf(wordId) }
        wordFrag.sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 400
            drawingViewId = R.id.dict_container
            setAllContainerColors(requireContext().getColorAttr(android.R.attr.colorBackground))
        }
        // run transaction
        childFragmentManager.commit {
            replace(R.id.dict_container, wordFrag)
            addSharedElement(srcView, getString(R.string.transit_word_root))
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onWordDeleted(undo: suspend () -> Unit) {
        childFragmentManager.popBackStack()
        Snackbar.make(requireView(), R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { lifecycleScope.launch { undo() } }
            .show()
    }

    override fun openAddWord(srcView: View) = host<Router>().openAddWord(srcView)
}