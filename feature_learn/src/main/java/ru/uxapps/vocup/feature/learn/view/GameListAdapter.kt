package ru.uxapps.vocup.feature.learn.view

import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.inflateBind
import ru.uxapps.vocup.feature.learn.databinding.ItemGameBinding
import ru.uxapps.vocup.feature.learn.model.GameItem

internal class GameListAdapter(
    private val onStartClick: (GameItem) -> Unit
) : ListAdapter<GameItem, GameListAdapter.ExVh>(
    object : DiffUtil.ItemCallback<GameItem>() {
        override fun areItemsTheSame(oldItem: GameItem, newItem: GameItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: GameItem, newItem: GameItem) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ExVh(parent.inflateBind(ItemGameBinding::inflate))

    override fun onBindViewHolder(holder: ExVh, position: Int) = holder.bind(getItem(position))

    inner class ExVh(private val bind: ItemGameBinding) : ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener {
                val item = getItem(adapterPosition)
                if (item.error == null) {
                    onStartClick(item)
                } else {
                    bind.gameError.alpha = 1f
                    bind.gameError.animate().alpha(.3f).apply {
                        interpolator = CycleInterpolator(3f)
                        duration = 2000
                    }
                }
            }
        }

        fun bind(item: GameItem) = with(bind) {
            gameTitle.text = item.title
            gameDesc.text = item.desc
            gameError.text = item.error
            gameError.animate().cancel()
            gameError.isVisible = item.error != null
            gameError.alpha = 1f
        }
    }
}