package ru.uxapps.vocup.feature.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.feature.dictionary.dict.DictFragment
import ru.uxapps.vocup.feature.dictionary.worddetails.WordFragment
import ru.uxapps.vocup.feature.router

class DictWorkflow : Fragment(R.layout.workflow_dict), DictFragment.Router {

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

    override fun openWord(text: String, target: Fragment) {
        childFragmentManager.commit {
            replace(R.id.dict_container, WordFragment().apply {
                arguments = WordFragment.argsOf(text)
                setTargetFragment(target, 0)
            })
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }

    override fun openAddWord() = router<Router>().openAddWord()
}