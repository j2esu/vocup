package ru.uxapps.vocup.feature.learn

import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.learn.databinding.FragmentLearnBinding
import ru.uxapps.vocup.feature.learn.di.LearnViewModel
import ru.uxapps.vocup.feature.learn.model.ExerciseItem
import ru.uxapps.vocup.feature.learn.model.Exercises
import ru.uxapps.vocup.feature.learn.view.LearnView
import javax.inject.Inject

class LearnFragment : BaseFragment(R.layout.fragment_learn) {

    private val vm by viewModels<LearnViewModel>()

    @Inject internal lateinit var exercises: Exercises

    override fun onViewReady(view: View, init: Boolean) {
        vm.learnComponent.inject(this)
        val learnView = LearnView(FragmentLearnBinding.bind(view), object : LearnView.Callback {

            override fun onStart(item: ExerciseItem) {
                Toast.makeText(context, "Open ${getString(item.exercise.title)}", Toast.LENGTH_SHORT).show()
            }

            override fun onStartRandom() {
                Toast.makeText(context, "Start random exercise", Toast.LENGTH_SHORT).show()
            }
        })
        exercises.exercises.observe(viewLifecycleOwner, learnView::setExercises)
    }
}