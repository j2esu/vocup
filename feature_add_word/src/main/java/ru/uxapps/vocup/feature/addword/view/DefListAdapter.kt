package ru.uxapps.vocup.feature.addword.view

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.feature.addword.R
import ru.uxapps.vocup.feature.addword.databinding.ItemDefBinding
import ru.uxapps.vocup.feature.addword.model.AddWord.DefItem
import ru.uxapps.vocup.feature.getString
import ru.uxapps.vocup.feature.inflateBind

internal class DefListAdapter(
    private val onClick: (DefItem, View) -> Unit
) :
    ListAdapter<DefItem, DefListAdapter.DefVh>(
        object : DiffUtil.ItemCallback<DefItem>() {
            override fun areItemsTheSame(oldItem: DefItem, newItem: DefItem) =
                oldItem.text == newItem.text

            override fun areContentsTheSame(oldItem: DefItem, newItem: DefItem) = oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DefVh(parent.inflateBind(ItemDefBinding::inflate))

    override fun onBindViewHolder(holder: DefVh, position: Int) = holder.bind(getItem(position))

    inner class DefVh(private val bind: ItemDefBinding) : ViewHolder(bind.root) {

        init {
            bind.defBg.setOnClickListener { onClick(getItem(adapterPosition), itemView) }
        }

        fun bind(item: DefItem) = with(bind) {
            defText.text = item.text
            defTrans.apply {
                val trans = item.trans
                when {
                    trans == null -> isVisible = false
                    trans.isEmpty() -> {
                        isVisible = true
                        isEnabled = false
                        setText(R.string.no_translations_found)
                    }
                    else -> {
                        isVisible = true
                        isEnabled = true
                        text = trans.joinToString(separator = "\n") {
                            val pattern = if (it.second) R.string.trans_pattern else R.string.new_trans_pattern
                            getString(pattern, it.first)
                        }
                    }
                }
            }
            defSaved.apply {
                isVisible = item.wordId != null
                isActivated = item.trans?.all { it.second } == true
            }
            root.transitionName = getString(R.string.transit_def_item_pattern, item.text)
        }
    }
}