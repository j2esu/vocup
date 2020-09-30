package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.addword.AddListFragment
import ru.uxapps.vocup.feature.dictionary.WordFragment

class AddListWorkflow : BaseFragment(R.layout.workflow_add_list), AddListFragment.Router, WordFragment.Router {

    companion object {
        fun argsOf(title: String, list: List<String>) = AddListFragment.argsOf(title, list)
    }

    override fun onViewReady(view: View, init: Boolean) {
        if (init) {
            childFragmentManager.commitNow {
                add(R.id.add_list_container, AddListFragment::class.java, arguments)
            }
        }
    }

    override fun openWord(wordId: Long, srcView: View) {
        val wordFragment = WordFragment().apply { arguments = WordFragment.argsOf(wordId) }
        childFragmentManager.commit {
            replace(R.id.add_list_container, wordFragment)
            addToBackStack(null)
        }
    }

    override fun onWordDeleted(undo: suspend () -> Unit) {
        childFragmentManager.popBackStack()
        Snackbar.make(requireView(), R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { lifecycleScope.launch { undo() } }
            .show()
    }
}