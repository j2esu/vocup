package ru.uxapps.vocup.feature.explore.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.explore.R
import ru.uxapps.vocup.feature.explore.databinding.ItemKitBinding
import ru.uxapps.vocup.feature.explore.model.KitItem
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.inflateBind

internal class KitListAdapter(
    private val onClick: (KitItem, View) -> Unit
) : ListAdapter<KitItem, KitListAdapter.KitVh>(
    object : DiffUtil.ItemCallback<KitItem>() {
        override fun areItemsTheSame(oldItem: KitItem, newItem: KitItem) = oldItem.kit.id == newItem.kit.id
        override fun areContentsTheSame(oldItem: KitItem, newItem: KitItem) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        KitVh(parent.inflateBind(ItemKitBinding::inflate))

    override fun onBindViewHolder(holder: KitVh, position: Int) = holder.bind(getItem(position))

    inner class KitVh(private val bind: ItemKitBinding) : ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener { onClick(getItem(adapterPosition), it) }
        }

        fun bind(item: KitItem) = with(bind) {
            kitTitle.text = item.kit.title
            if (item.newWords.isEmpty()) {
                kitWords.setText(R.string.no_new_words)
            } else {
                val words = item.newWords.joinToString(", ")
                kitWords.text = getString(R.string.new_words_pattern, words)
            }
        }
    }
}