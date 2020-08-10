package ru.uxapps.vocup.screen.words

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordsBinding
import ru.uxapps.vocup.databinding.ItemWordBinding

interface WordsView {
    fun setWords(words: List<Word>)
    fun setLoading(loading: Boolean)
}

class WordsViewImp(
    private val binding: FragmentWordsBinding,
    onWordClick: (Word) -> Unit,
    onAddWordClick: () -> Unit
) : WordsView {

    private val adapter = WordsListAdapter(onWordClick)

    init {
        binding.wordsList.apply {
            adapter = this@WordsViewImp.adapter
            layoutManager = LinearLayoutManager(context)
        }
        binding.wordsAdd.setOnClickListener { onAddWordClick() }
    }

    override fun setWords(words: List<Word>) {
        adapter.submitList(words)
        binding.wordsEmpty.isVisible = words.isEmpty()
    }

    override fun setLoading(loading: Boolean) {
        binding.wordsProgress.isVisible = loading
    }
}

private class WordsListAdapter(
    private val onWordClick: (Word) -> Unit
) : ListAdapter<Word, WordsListAdapter.WordVh>(object : DiffUtil.ItemCallback<Word>() {
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