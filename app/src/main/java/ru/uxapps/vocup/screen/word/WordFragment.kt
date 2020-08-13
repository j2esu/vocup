package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordBinding

class WordFragment : Fragment(R.layout.fragment_word) {

    companion object Args {
        fun argsOf(word: Word) = bundleOf("word" to word.trans.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<WordViewModel>()
        val binding = FragmentWordBinding.bind(view)
        with(vm.wordDetails(wordText)) {
            word.observe(viewLifecycleOwner) {
                binding.wordText.text = it
            }
            details.observe(viewLifecycleOwner) {
                binding.wordDetails.text = it?.trans?.meanings?.joinToString(", ")
                    ?: getString(R.string.loading_details)
            }
        }
    }
}