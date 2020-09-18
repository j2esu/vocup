package ru.uxapps.vocup.feature.learn.view

import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.feature.learn.databinding.FragmentLearnBinding
import ru.uxapps.vocup.feature.learn.model.ExerciseItem

internal class LearnView(
    bind: FragmentLearnBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onStart(item: ExerciseItem)
        fun onStartRandom()
    }

    private val exAdapter = ExerciseListAdapter { callback.onStart(it) }

    init {
        bind.learnRv.apply {
            adapter = exAdapter
            layoutManager = LinearLayoutManager(context)
        }
        bind.learnStartRandom.setOnClickListener { callback.onStartRandom() }
    }

    fun setExercises(exercises: List<ExerciseItem>) {
        exAdapter.submitList(exercises)
    }
}