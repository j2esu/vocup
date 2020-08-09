package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.data.repo
import java.io.IOException

class WordFragment : Fragment(R.layout.fragment_word) {

    companion object Args {
        fun argsOf(word: Word) = bundleOf("word_text" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word_text"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // init word text
        view.findViewById<TextView>(R.id.wordText).text = wordText
        // init translation
        val transTv = view.findViewById<TextView>(R.id.wordTranslation)
        val loadTranslation = {
            transTv.isEnabled = false
            transTv.setText(R.string.loading_translation)
            lifecycleScope.launchWhenStarted {
                try {
                    transTv.text = repo.getTranslation(wordText)
                } catch (e: IOException) {
                    transTv.setText(R.string.reload_translation)
                }
                transTv.isEnabled = true
            }
        }
        transTv.setOnClickListener { loadTranslation() }
        loadTranslation() // load translation immediately
    }
}