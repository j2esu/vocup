package ru.uxapps.vocup.feature.learn.view

import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.inflateBind
import ru.uxapps.vocup.feature.learn.R
import ru.uxapps.vocup.feature.learn.databinding.ItemGameBinding
import ru.uxapps.vocup.feature.learn.model.GameItem

internal class GameListAdapter(
    private val onStartClick: (GameItem, View) -> Unit
) : ListAdapter<GameItem, GameListAdapter.GameVh>(
    object : DiffUtil.ItemCallback<GameItem>() {
        override fun areItemsTheSame(oldItem: GameItem, newItem: GameItem) = oldItem.game == newItem.game
        override fun areContentsTheSame(oldItem: GameItem, newItem: GameItem) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GameVh(parent.inflateBind(ItemGameBinding::inflate))

    override fun onBindViewHolder(holder: GameVh, position: Int) = holder.bind(getItem(position))

    inner class GameVh(private val bind: ItemGameBinding) : ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener {
                val item = getItem(adapterPosition)
                if (item.error == null) {
                    onStartClick(item, it)
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
            gameTitle.setText(item.game.title)
            gameTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(item.game.type.icon, 0, 0, 0)
            gameDesc.setText(item.game.desc)
            gameError.text = item.error
            gameError.animate().cancel()
            gameError.isVisible = item.error != null
            gameError.alpha = 1f
            root.transitionName = getString(R.string.transit_game_item_pattern, item.game.id)
        }
    }
}
