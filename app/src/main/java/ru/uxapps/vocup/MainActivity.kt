package ru.uxapps.vocup

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import ru.uxapps.vocup.feature.dictionary.AddWordWorkflow
import ru.uxapps.vocup.feature.dictionary.screen.word.WordFragment
import ru.uxapps.vocup.workflow.NavWorkflow

class MainActivity : AppCompatActivity(R.layout.activity_main), NavWorkflow.Router {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_container, NavWorkflow().also { setPrimaryNavigationFragment(it) })
            }
        }
        // configure input mode
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    window.setSoftInputMode(
                        when (f) {
                            is WordFragment -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                            is DialogFragment -> window.attributes.softInputMode
                            else -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        }
                    )
                }
            },
            true
        )
    }

    override fun openAddWord() {
        supportFragmentManager.commit {
            replace(R.id.main_container, AddWordWorkflow().also { setPrimaryNavigationFragment(it) })
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }
}