package ru.uxapps.vocup

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import ru.uxapps.vocup.feature.worddetails.WordFragment
import ru.uxapps.vocup.workflow.AddWordWorkflow

class MainActivity : AppCompatActivity(R.layout.activity_main), NavFragment.Router {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_container, NavFragment().also { setPrimaryNavigationFragment(it) })
            }
        }
        // configure input mode
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    @Suppress("DEPRECATION")
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