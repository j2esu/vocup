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
        fun argsOf(word: Word) = bundleOf("word" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<WordViewModel> { WordViewModel.createFactory(wordText) }
        val v = WordViewImp(FragmentWordBinding.bind(view), object : WordView.Callback {
            override fun onRetryClick() = vm.onRetry()
        })
        vm.word.observe(viewLifecycleOwner, v::setWordText)
        vm.translation.observe(viewLifecycleOwner, v::setTranslation)
    }
}