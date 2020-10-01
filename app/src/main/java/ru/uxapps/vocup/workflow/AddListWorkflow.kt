package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.addword.AddListFragment
import ru.uxapps.vocup.feature.awaitReady
import ru.uxapps.vocup.feature.dictionary.WordFragment
import ru.uxapps.vocup.feature.getColorAttr

class AddListWorkflow : BaseFragment(R.layout.workflow_add_list), AddListFragment.Router, WordFragment.Router {

    companion object {
        fun argsOf(title: String, list: List<String>) = AddListFragment.argsOf(title, list)
    }

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            val addListFragment = AddListFragment().apply { arguments = this@AddListWorkflow.arguments }
            childFragmentManager.commit {
                add(R.id.add_list_container, addListFragment)
            }
            postponeUntil {
                addListFragment.awaitReady()
            }
        }
    }

    override fun openWord(wordId: Long, srcView: View) {
        // exit
        val addListFragment = childFragmentManager.findFragmentById(R.id.add_list_container) as AddListFragment
        addListFragment.exitTransition = MaterialElevationScale(false)
        addListFragment.postpone()
        // enter
        val wordFragment = WordFragment().apply { arguments = WordFragment.argsOf(wordId) }
        wordFragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
            setAllContainerColors(requireContext().getColorAttr(android.R.attr.colorBackground))
        }
        // transition
        childFragmentManager.commit {
            replace(R.id.add_list_container, wordFragment)
            addSharedElement(srcView, getString(R.string.transit_word_root))
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    override fun onWordDeleted(undo: suspend () -> Unit) {
        childFragmentManager.popBackStack()
        Snackbar.make(requireView(), R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { lifecycleScope.launch { undo() } }
            .show()
    }
}