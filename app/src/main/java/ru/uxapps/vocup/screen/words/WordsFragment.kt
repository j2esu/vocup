package ru.uxapps.vocup.screen.words

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.RepoProvider

class WordsFragment : Fragment(R.layout.fragment_words) {

    private val repo = RepoProvider.provideRepo()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initWordsList(view)
    }

    private fun initWordsList(view: View) {
        val rv = view.findViewById<RecyclerView>(R.id.wordsList)
        val adapter = WordsListAdapter {
            Toast.makeText(context, "Click on $it", Toast.LENGTH_SHORT).show()
        }
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)
        repo.getAllWords().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}