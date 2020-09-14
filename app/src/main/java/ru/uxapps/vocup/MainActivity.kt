package ru.uxapps.vocup

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.transition.ArcMotion
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.google.android.material.transition.Hold
import com.google.android.material.transition.MaterialContainerTransform
import ru.uxapps.vocup.feature.SoftInputImp
import ru.uxapps.vocup.feature.SoftInputProvider
import ru.uxapps.vocup.feature.getColorAttr
import ru.uxapps.vocup.feature.worddetails.WordFragment
import ru.uxapps.vocup.workflow.AddWordWorkflow
import ru.uxapps.vocup.workflow.NavWorkflow

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
        configureInputMode()
    }

    private fun configureInputMode() {
        // need custom ime settings in some fragments for better ui
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
                    when (f) {
                        is WordFragment -> {
                            window.setSoftInputMode(SOFT_INPUT_ADJUST_PAN)
                        }
                        !is DialogFragment -> {
                            window.setSoftInputMode(SOFT_INPUT_ADJUST_RESIZE)
                        }
                    }
                }
            },
            true
        )
    }

    override fun openAddWord(srcView: View) {
        // exit
        val navWorkflow = supportFragmentManager.findFragmentById(R.id.main_container) as NavWorkflow
        navWorkflow.exitTransition = TransitionSet()
            .addTransition(Hold())
            .addTransition(Slide(Gravity.BOTTOM).apply {
                addTarget(getString(R.string.transit_nav_bar))
            })
        navWorkflow.postpone()
        // enter
        val addWordWorkflow = AddWordWorkflow()
        addWordWorkflow.sharedElementEnterTransition = MaterialContainerTransform().apply {
            startContainerColor = getColorAttr(R.attr.colorSecondary)
            endContainerColor = getColorAttr(android.R.attr.colorBackground)
            softInput.nextShowDelay = 350
            setPathMotion(ArcMotion())
        }
        // transaction
        supportFragmentManager.commit {
            replace(R.id.main_container, addWordWorkflow)
            setPrimaryNavigationFragment(addWordWorkflow)
            addSharedElement(srcView, getString(R.string.transit_add_word_root))
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        softInput.hide()
    }
}