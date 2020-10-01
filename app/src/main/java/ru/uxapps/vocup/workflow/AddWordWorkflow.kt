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
import ru.uxapps.vocup.feature.addword.AddWordFragment
import ru.uxapps.vocup.feature.awaitReady
import ru.uxapps.vocup.feature.dictionary.WordFragment
import ru.uxapps.vocup.feature.getColorAttr

class AddWordWorkflow : BaseFragment(R.layout.workflow_add_word), AddWordFragment.Router, WordFragment.Router {

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            val addWordFragment = AddWordFragment()
            childFragmentManager.commit {
                add(R.id.add_word_container, addWordFragment)
                setReorderingAllowed(true)
            }
            postponeUntil {
                addWordFragment.awaitReady()
            }
        }
    }

    override fun openWord(wordId: Long, srcView: View) {
        // exit
        val addWordFragment = childFragmentManager.findFragmentById(R.id.add_word_container) as AddWordFragment
        addWordFragment.exitTransition = MaterialElevationScale(false).apply {
            duration = 400
        }
        addWordFragment.postpone()
        // enter
        val wordFragment = WordFragment().apply { arguments = WordFragment.argsOf(wordId) }
        wordFragment.sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = 400
            setAllContainerColors(requireContext().getColorAttr(android.R.attr.colorBackground))
        }
        // transition
        childFragmentManager.commit {
            replace(R.id.add_word_container, wordFragment)
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