package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.data.repo
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.feature.TranslationFeature.State.*
import ru.uxapps.vocup.util.clicks

class WordFragment : Fragment(R.layout.fragment_word) {

    companion object Args {
        fun argsOf(word: Word) = bundleOf("word" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // init word text
        view.findViewById<TextView>(R.id.wordText).text = wordText
        // init translation
        val trans = TranslationFeature(repo)
        val transTv = view.findViewById<TextView>(R.id.wordTranslation)
        val transFlow = transTv.clicks(lifecycleScope).consumeAsFlow()
            .onStart { emit(Unit) } // immediately load translation
            .flatMapLatest { trans.getTranslation(wordText) }
        lifecycleScope.launchWhenStarted {
            transFlow.collect {
                transTv.isEnabled = it is Fail
                when (it) {
                    is Success -> transTv.text = it.result
                    Fail -> transTv.setText(R.string.reload_translation)
                    Progress -> transTv.setText(R.string.loading_translation)
                }
            }
        }
    }
}