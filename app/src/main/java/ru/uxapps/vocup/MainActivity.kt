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
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

class MainActivity : AppCompatActivity(R.layout.activity_main), Navigation, WordFragment.Host {

    override val onDeleteWord = MutableLiveEvent<Word>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_container, NavFragment())
            }
        }
    }

    override fun openWord(word: Word) {
        supportFragmentManager.commit {
            supportFragmentManager.findFragmentById(R.id.main_container)?.let(::hide)
            add(R.id.main_container, WordFragment::class.java, WordFragment.argsOf(word))
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }

    override fun openAddWord() {
        supportFragmentManager.commit {
            supportFragmentManager.findFragmentById(R.id.main_container)?.let(::hide)
            add(R.id.main_container, AddWordFragment())
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }

    override fun up() = onBackPressed()

    override fun onDeleteWord(word: Word) {
        supportFragmentManager.popBackStack()
        onDeleteWord.send(word)
    }
}