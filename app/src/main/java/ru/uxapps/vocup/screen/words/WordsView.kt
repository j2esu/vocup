package ru.uxapps.vocup.screen.words

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word

interface WordsView {
    fun setWords(words: List<Word>)
    fun setLoading(loading: Boolean)
}

class WordsViewImp(
    root: View,
    onWordClick: (Word) -> Unit,
    onAddWordClick: () -> Unit
) : WordsView {

    private val adapter = WordsListAdapter(onWordClick)
    private val loadingPb = root.findViewById<View>(R.id.wordsProgress)
    private val emptyView = root.findViewById<View>(R.id.wordsEmpty)

    init {
        // init rv
        val rv = root.findViewById<RecyclerView>(R.id.wordsList)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(root.context)
        // init add word button
        root.findViewById<View>(R.id.wordsAdd).setOnClickListener { onAddWordClick() }
    }

    override fun setWords(words: List<Word>) {
        adapter.submitList(words)
        emptyView.isVisible = words.isEmpty()
    }

    override fun setLoading(loading: Boolean) {
        loadingPb.isVisible = loading
    }
}