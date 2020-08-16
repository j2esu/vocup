package ru.uxapps.vocup.screen.dict

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.ItemWordBinding
import ru.uxapps.vocup.util.inflateBinding

class WordListAdapter(
    private val onWordClick: (Word) -> Unit
) : ListAdapter<Word, WordListAdapter.WordVh>(object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.text == newItem.text
    override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WordVh(parent.inflateBinding(ItemWordBinding::inflate))

    override fun onBindViewHolder(holder: WordVh, position: Int) = holder.bind(getItem(position))

    inner class WordVh(private val binding: ItemWordBinding) : ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onWordClick(getItem(adapterPosition)) }
        }

        fun bind(word: Word) = with(binding) {
            wordText.text = word.text
            wordTransCount.text = root.resources.getString(R.string.trans_count_pattern,
                word.translations.size)
        }
    }
}