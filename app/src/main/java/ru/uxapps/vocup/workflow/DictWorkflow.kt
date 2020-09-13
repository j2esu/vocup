package ru.uxapps.vocup.workflow

import android.view.View
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitReady
import ru.uxapps.vocup.feature.dictionary.DictFragment
import ru.uxapps.vocup.transition.loadTransition
import ru.uxapps.vocup.feature.worddetails.WordFragment
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

    override fun openWord(text: String, srcView: View) {
        // setup exit
        val dictFrag = childFragmentManager.findFragmentById(R.id.dict_container) as DictFragment
        dictFrag.exitTransition = loadTransition(R.transition.open_word_exit)
        dictFrag.postpone()
        // setup enter
        val wordFrag = WordFragment().apply { arguments = WordFragment.argsOf(text) }
        wordFrag.sharedElementEnterTransition = loadTransition(R.transition.open_word_shared_enter)
        wordFrag.sharedElementReturnTransition = loadTransition(R.transition.open_word_shared_return)
        wordFrag.postpone()
        // run transaction
        childFragmentManager.commit {
            replace(R.id.dict_container, wordFrag)
            addSharedElement(srcView, getString(R.string.trans_word_root))
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