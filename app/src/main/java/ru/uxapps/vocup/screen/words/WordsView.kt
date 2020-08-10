package ru.uxapps.vocup.screen.words

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordsBinding

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