package ru.uxapps.vocup.feature.addword.view

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.addword.databinding.ItemCompletionBinding
import ru.uxapps.vocup.feature.inflateBind

internal class CompletionAdapter(
    private val onClick: (String) -> Unit
) : ListAdapter<String, CompletionAdapter.CompVh>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = true
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CompVh(parent.inflateBind(ItemCompletionBinding::inflate))

    override fun onBindViewHolder(holder: CompVh, position: Int) = holder.bind(getItem(position))

    inner class CompVh(private val bind: ItemCompletionBinding) : ViewHolder(bind.root) {

        init {
            bind.root.setOnClickListener { onClick(getItem(adapterPosition)) }
        }

        fun bind(text: String) {
            bind.compText.text = text
        }
    }
}