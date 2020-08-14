package ru.uxapps.vocup.screen.addword

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord.TransItem
import ru.uxapps.vocup.databinding.ItemTransBinding
import ru.uxapps.vocup.util.inflateBinding

class TransListAdapter(
    private val onSave: (TransItem) -> Unit,
    private val onRemove: (TransItem) -> Unit
) :
    ListAdapter<TransItem, TransListAdapter.TransVh>(
        object : DiffUtil.ItemCallback<TransItem>() {
            override fun areItemsTheSame(oldItem: TransItem, newItem: TransItem) =
                oldItem.trans == newItem.trans

            override fun areContentsTheSame(oldItem: TransItem, newItem: TransItem) =
                oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TransVh(parent.inflateBinding(ItemTransBinding::inflate))

    override fun onBindViewHolder(holder: TransVh, position: Int) = holder.bind(getItem(position))

    inner class TransVh(private val binding: ItemTransBinding) : ViewHolder(binding.root) {

        init {
            binding.transBg.setOnClickListener {
                if (!binding.transSaved.isActivated) {
                    onSave(getItem(adapterPosition))
                } else {
                    onRemove(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: TransItem) = with(binding) {
            transText.text = item.trans.text
            transMeanings.text = item.trans.meanings.joinToString(separator = "\n") {
                root.resources.getString(R.string.trans_meaning_pattern, it)
            }
            transSaved.isActivated = item.saved
        }
    }
}