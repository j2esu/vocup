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
import ru.uxapps.vocup.nav

class WordFragment : Fragment(R.layout.fragment_word) {

    interface Host {
        fun onDeleteWord(word: Word)
    }

    companion object {
        fun argsOf(word: Word) = bundleOf("word" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<WordViewModel>()
        val v = WordView(FragmentWordBinding.bind(view), object : WordView.Callback {
            override fun onWordNotFound() = nav.up()
            override fun onUp() = nav.up()
            override fun onDelete(word: Word) = (activity as Host).onDeleteWord(word)
        })
        with(vm.wordDetails(wordText)) {
            details.observe(viewLifecycleOwner, v::setDetails)
            word.observe(viewLifecycleOwner, v::setWordText)
        }
    }
}