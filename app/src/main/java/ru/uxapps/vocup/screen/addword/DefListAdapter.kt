package ru.uxapps.vocup.screen.addword

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.databinding.ItemDefBinding
import ru.uxapps.vocup.util.inflateBind

class DefListAdapter(
    private val onSave: (DefItem) -> Unit,
    private val onRemove: (DefItem) -> Unit
) :
    ListAdapter<DefItem, DefListAdapter.DefVh>(
        object : DiffUtil.ItemCallback<DefItem>() {
            override fun areItemsTheSame(oldItem: DefItem, newItem: DefItem) =
                oldItem.def == newItem.def

            override fun areContentsTheSame(oldItem: DefItem, newItem: DefItem) = oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DefVh(parent.inflateBind(ItemDefBinding::inflate))

    override fun onBindViewHolder(holder: DefVh, position: Int) = holder.bind(getItem(position))

    inner class DefVh(private val bind: ItemDefBinding) : ViewHolder(bind.root) {

        init {
            bind.defBg.setOnClickListener {
                if (!bind.defSaved.isActivated) {
                    onSave(getItem(adapterPosition))
                } else {
                    onRemove(getItem(adapterPosition))
                }
            }
        }

        fun bind(item: DefItem) = with(bind) {
            defText.text = item.def.text
            defTranslations.text = item.def.translations.joinToString(separator = "\n") {
                root.resources.getString(R.string.trans_pattern, it)
            }
            defSaved.isActivated = item.saved
        }
    }
}