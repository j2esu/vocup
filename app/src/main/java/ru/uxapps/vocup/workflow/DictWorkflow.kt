package ru.uxapps.vocup.workflow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.dictionary.DictFragment
import ru.uxapps.vocup.feature.loadTransition
import ru.uxapps.vocup.feature.worddetails.WordFragment
import ru.uxapps.vocup.util.host

class DictWorkflow : Fragment(R.layout.workflow_dict), DictFragment.Router, WordFragment.Router {

    interface Router {
        fun openAddWord()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.dict_container, DictFragment())
            }
        }
    }

    override fun openWord(text: String, srcItem: View) {
        // setup exit
        val dictFrag = childFragmentManager.findFragmentById(R.id.dict_container) as DictFragment
        dictFrag.exitTransition = loadTransition(R.transition.open_word_exit)
        // setup enter
        val wordFrag = WordFragment().apply { arguments = WordFragment.argsOf(text) }
        wordFrag.sharedElementEnterTransition = loadTransition(R.transition.open_word_enter_shared)
        // run transaction
        childFragmentManager.commit {
            replace(R.id.dict_container, wordFrag)
            addSharedElement(srcItem, "word_root")
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

    override fun openAddWord() = host<Router>().openAddWord()
}