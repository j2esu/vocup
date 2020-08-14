package ru.uxapps.vocup.screen.word

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.databinding.ItemMeanBinding
import ru.uxapps.vocup.util.inflateBinding

class MeaningListAdapter : ListAdapter<String, MeaningListAdapter.MeanVh>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = true
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MeanVh(parent.inflateBinding(ItemMeanBinding::inflate))

    override fun onBindViewHolder(holder: MeanVh, position: Int) = holder.bind(getItem(position))

    inner class MeanVh(private val binding: ItemMeanBinding) : ViewHolder(binding.root) {

        fun bind(meaning: String) = with(binding) {
            meanText.text = meaning
        }
    }
}