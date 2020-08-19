package ru.uxapps.vocup

import android.os.Bundle
import android.view.WindowManager.LayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.screen.Navigation
import ru.uxapps.vocup.screen.addword.AddWordFragment
import ru.uxapps.vocup.screen.nav.NavFragment
import ru.uxapps.vocup.screen.word.WordFragment
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

class MainActivity : AppCompatActivity(R.layout.activity_main), Navigation, WordFragment.Host {

    override val onDeleteWord = MutableLiveEvent<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_container, NavFragment())
            }
        }
        // set soft input mode for different fragments
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
                    if (f is AddWordFragment) {
                        window.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_PAN)
                    }
                }

                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    if (f is AddWordFragment) {
                        window.setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    }
                }
            }, false
        )
    }

    override fun openWord(word: String) {
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

    override fun onDeleteWord(text: String) {
        supportFragmentManager.popBackStack()
        onDeleteWord.send(text)
    }
}