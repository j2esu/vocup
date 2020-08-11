package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.Translation
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordBinding

class WordFragment : Fragment(R.layout.fragment_word) {

    companion object Args {
        fun argsOf(word: Word) = bundleOf("word" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<WordViewModel>()
        val binding = FragmentWordBinding.bind(view)
        with(vm.wordDetails(wordText)) {
            binding.wordTranslation.setOnClickListener { onRetry() }
            word.observe(viewLifecycleOwner) {
                binding.wordText.text = it
            }
            translation.observe(viewLifecycleOwner) {
                binding.wordTranslation.isEnabled = it is Translation.State.Fail
                when (it) {
                    is Translation.State.Success -> binding.wordTranslation.text = it.result
                    Translation.State.Fail -> binding.wordTranslation.setText(R.string.reload_translation)
                    Translation.State.Progress -> binding.wordTranslation.setText(R.string.loading_translation)
                }
            }
        }
    }
}