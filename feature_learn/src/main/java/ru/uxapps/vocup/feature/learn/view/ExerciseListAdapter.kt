package ru.uxapps.vocup.feature.learn.view

import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.inflateBind
import ru.uxapps.vocup.feature.learn.databinding.ItemExerciseBinding
import ru.uxapps.vocup.feature.learn.model.ExerciseItem

internal class ExerciseListAdapter(
    private val onStartClick: (ExerciseItem) -> Unit
) : ListAdapter<ExerciseItem, ExerciseListAdapter.ExVh>(
    object : DiffUtil.ItemCallback<ExerciseItem>() {
        override fun areItemsTheSame(oldItem: ExerciseItem, newItem: ExerciseItem) =
            oldItem.exercise == newItem.exercise

        override fun areContentsTheSame(oldItem: ExerciseItem, newItem: ExerciseItem) =
            oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExVh(parent.inflateBind(ItemExerciseBinding::inflate))

    override fun onBindViewHolder(holder: ExVh, position: Int) = holder.bind(getItem(position))

    inner class ExVh(private val bind: ItemExerciseBinding) : ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener {
                if (it.isActivated) {
                    onStartClick(getItem(adapterPosition))
                } else {
                    bind.exerciseRequirement.translationX = 0f
                    bind.exerciseRequirement.animate().translationX(8f).apply {
                        interpolator = CycleInterpolator(1.5f)
                        duration = 200
                    }
                }
            }
        }

        fun bind(item: ExerciseItem) = with(bind) {
            exerciseTitle.setText(item.exercise.title)
            exerciseDesc.setText(item.exercise.desc)
            bind.root.isActivated = item.enabled
            exerciseRequirement.isVisible = !item.enabled
            exerciseRequirement.setText(item.exercise.requirement)
        }
    }
}