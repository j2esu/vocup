package ru.uxapps.vocup.screen.words

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.Word

class WordsFragment : Fragment(R.layout.fragment_words) {

    interface Host {
        fun openWord(word: Word)
        fun openAddWord()
    }

    private val repo = RepoProvider.provideRepo()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initWordsList(view)
        initAddWordButton(view)
    }

    private fun initWordsList(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.wordsList)
        val adapter = WordsListAdapter {
            (activity as Host).openWord(it)
        }
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        val loadingPb = view.findViewById<View>(R.id.wordsProgress)
        val emptyView = view.findViewById<View>(R.id.wordsEmpty)
        repo.getAllWords().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            emptyView.isVisible = it.isEmpty()
            loadingPb.isVisible = false
        }
    }

    private fun initAddWordButton(view: View) {
        val btn = view.findViewById<View>(R.id.wordsAdd)
        btn.setOnClickListener {
            (activity as Host).openAddWord()
        }
    }
}