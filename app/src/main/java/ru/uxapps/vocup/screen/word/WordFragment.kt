package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.Word
import java.io.IOException

class WordFragment : Fragment(R.layout.fragment_word) {

    companion object {

        private const val ARG_WORD_TEXT = "ARG_WORD_TEXT"

        fun newInstance(word: Word) = WordFragment().apply {
            arguments = bundleOf(ARG_WORD_TEXT to word.text)
        }
    }

    private val repo = RepoProvider.provideRepo()

    private val textTv by lazy { requireView().findViewById<TextView>(R.id.wordText) }
    private val transTv by lazy { requireView().findViewById<TextView>(R.id.wordTranslation) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val wordText = requireArguments().getString(ARG_WORD_TEXT) ?: error("No word text")
        textTv.text = wordText
        transTv.setOnClickListener { loadTranslation(wordText) }
        loadTranslation(wordText)
    }

    private fun loadTranslation(text: String) {
        transTv.isEnabled = false
        transTv.setText(R.string.loading_translation)
        lifecycleScope.launchWhenStarted {
            try {
                transTv.text = repo.getTranslation(text)
            } catch (e: IOException) {
                transTv.setText(R.string.cant_load_translation)
            }
            transTv.isEnabled = true
        }
    }
}