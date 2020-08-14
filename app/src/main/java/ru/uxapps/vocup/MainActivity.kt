package ru.uxapps.vocup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.screen.Navigation
import ru.uxapps.vocup.screen.addword.AddWordFragment
import ru.uxapps.vocup.screen.nav.NavFragment
import ru.uxapps.vocup.screen.word.WordFragment

class MainActivity : AppCompatActivity(R.layout.activity_main), Navigation, WordFragment.Host {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.mainContainer, NavFragment())
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

    override fun up() = onBackPressed()

    override fun onWordNotFound(text: String) = supportFragmentManager.popBackStack()

}