package ru.uxapps.vocup.screen.word

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.databinding.ItemTransBinding
import ru.uxapps.vocup.util.inflateBinding

class TransListAdapter : ListAdapter<String, TransListAdapter.TransVh>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = true
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransVh(parent.inflateBinding(ItemTransBinding::inflate))

    override fun onBindViewHolder(holder: TransVh, position: Int) = holder.bind(getItem(position))

    inner class TransVh(private val binding: ItemTransBinding) : ViewHolder(binding.root) {

        fun bind(trans: String) = with(binding) {
            transText.text = trans
        }
    }
}