package ru.uxapps.vocup.screen.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.ItemWordBinding

class WordListAdapter(
    private val onWordClick: (Word) -> Unit
) : ListAdapter<Word, WordListAdapter.WordVh>(object : DiffUtil.ItemCallback<Word>() {
    override fun areItemsTheSame(oldItem: Word, newItem: Word) = oldItem.text == newItem.text
    override fun areContentsTheSame(oldItem: Word, newItem: Word) = oldItem == newItem
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordVh {
        return WordVh(ItemWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {
            onWordClick(getItem(it))
        }
    }

    override fun onBindViewHolder(holder: WordVh, position: Int) = holder.bind(getItem(position))

    class WordVh(private val binding: ItemWordBinding, onItemClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onItemClick(adapterPosition) }
        }

        fun bind(word: Word) {
            binding.wordText.text = word.text
        }
    }
}