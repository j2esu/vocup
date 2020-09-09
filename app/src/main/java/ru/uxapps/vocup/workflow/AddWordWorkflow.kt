package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.addword.AddWordFragment
import ru.uxapps.vocup.feature.worddetails.WordFragment

class AddWordWorkflow : BaseFragment(R.layout.workflow_add_word), AddWordFragment.Router, WordFragment.Router {

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            childFragmentManager.commitNow {
                add(R.id.add_word_container, AddWordFragment())
            }
        }
    }

    override fun openWord(text: String) {
        childFragmentManager.commit {
            replace(R.id.add_word_container, WordFragment::class.java, WordFragment.argsOf(text))
            addToBackStack(null)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }

    override fun onWordDeleted(undo: suspend () -> Unit) {
        childFragmentManager.popBackStack()
        Snackbar.make(requireView(), R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { lifecycleScope.launch { undo() } }
            .show()
    }
}