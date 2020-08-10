package ru.uxapps.vocup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.screen.Navigation
import ru.uxapps.vocup.screen.addword.AddWordFragment
import ru.uxapps.vocup.screen.word.WordFragment
import ru.uxapps.vocup.screen.words.WordsFragment

class MainActivity : AppCompatActivity(R.layout.activity_main), Navigation, AddWordFragment.Host {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.mainContainer, WordsFragment())
            }
        }
    }

    override fun openWord(word: Word) {
        supportFragmentManager.commit {
            replace(R.id.mainContainer, WordFragment::class.java, WordFragment.argsOf(word))
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }

    override fun openAddWord() {
        supportFragmentManager.commit {
            replace(R.id.mainContainer, AddWordFragment())
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }

    override fun onWordAdded(text: String) {
        Toast.makeText(this, getString(R.string.word_saved, text), Toast.LENGTH_SHORT).show()
        supportFragmentManager.popBackStack()
    }
}