package ru.uxapps.vocup.feature.dictionary

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.feature.dictionary.addword.AddWordFragment
import ru.uxapps.vocup.feature.dictionary.worddetails.WordFragment

class AddWordWorkflow : Fragment(R.layout.workflow_add_word), AddWordFragment.Router {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.add_word_container, AddWordFragment())
            }
        }
    }

    override fun openWord(text: String, target: Fragment) {
        childFragmentManager.commit {
            replace(R.id.add_word_container, WordFragment().apply {
                arguments = WordFragment.argsOf(text)
                setTargetFragment(target, 0)
            })
            addToBackStack(null)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        }
    }
}