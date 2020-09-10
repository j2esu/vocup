package ru.uxapps.vocup

import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import ru.uxapps.vocup.feature.SoftInputImp
import ru.uxapps.vocup.feature.SoftInputProvider
import ru.uxapps.vocup.feature.loadTransition
import ru.uxapps.vocup.feature.worddetails.WordFragment
import ru.uxapps.vocup.workflow.AddWordWorkflow

class MainActivity : AppCompatActivity(R.layout.activity_main), NavWorkflow.Router, SoftInputProvider {

    override val softInput by lazy { SoftInputImp(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val navWorkflow = NavWorkflow()
            supportFragmentManager.commitNow {
                add(R.id.main_container, navWorkflow)
                setPrimaryNavigationFragment(navWorkflow)
            }
        }
        // configure input mode (need custom settings in some fragments for better ui)
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    when (f) {
                        is WordFragment -> window.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)
                        !is DialogFragment -> window.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
                    }
                }
            },
            true
        )
    }

    override fun openAddWord(srcView: View) {
        // exit
        val navWorkflow = supportFragmentManager.findFragmentById(R.id.main_container) as NavWorkflow
        navWorkflow.exitTransition = loadTransition(R.transition.open_add_word_exit)
        navWorkflow.reenterTransition = loadTransition(R.transition.open_add_word_reenter)
        navWorkflow.postpone()
        // enter
        val addWordWorkflow = AddWordWorkflow()
        addWordWorkflow.sharedElementEnterTransition = loadTransition(R.transition.open_add_word_enter_shared)
            .also { softInput.nextShowDelay = it.duration }
        // transaction
        supportFragmentManager.commit {
            replace(R.id.main_container, addWordWorkflow)
            setPrimaryNavigationFragment(addWordWorkflow)
            addSharedElement(srcView, "add_word_toolbar")
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        softInput.hide()
    }
}