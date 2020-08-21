package ru.uxapps.vocup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import ru.uxapps.vocup.workflow.AddWordWorkflow
import ru.uxapps.vocup.workflow.NavWorkflow

class MainActivity : AppCompatActivity(R.layout.activity_main), NavWorkflow.Router {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_container, NavWorkflow().also { setPrimaryNavigationFragment(it) })
            }
        }
    }

    override fun openAddWord() {
        supportFragmentManager.commit {
            replace(R.id.main_container, AddWordWorkflow().also { setPrimaryNavigationFragment(it) })
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            addToBackStack(null)
        }
    }
}