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
        override fun areItemsTheSame(oldItem: GameItem, newItem: GameItem) = oldItem.game == newItem.game
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
                if (item.enabled) {
                    onStartClick(item)
                } else {
                    bind.gameReq.alpha = 1f
                    bind.gameReq.animate().alpha(.3f).apply {
                        interpolator = CycleInterpolator(3f)
                        duration = 2000
                    }
                }
            }
        }

        fun bind(item: GameItem) = with(bind) {
            gameTitle.setText(item.game.title)
            gameDesc.setText(item.game.desc)
            gameReq.setText(item.game.requirement)
            gameReq.animate().cancel()
            gameReq.isVisible = !item.enabled
            gameReq.alpha = 1f
        }
    }
}