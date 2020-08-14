package ru.uxapps.vocup.screen.word

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.WordDetails.State
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordBinding
import ru.uxapps.vocup.nav

class WordFragment : Fragment(R.layout.fragment_word) {

    interface Host {
        fun onWordNotFound(text: String)
    }

    companion object Args {
        fun argsOf(word: Word) = bundleOf("word" to word.text)
        private val WordFragment.wordText get() = requireArguments()["word"] as String
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<WordViewModel>()
        val binding = FragmentWordBinding.bind(view)
        with(vm.wordDetails(wordText)) {
            val listAdapter = TransListAdapter()
            binding.wordTransList.apply {
                adapter = listAdapter
                layoutManager = LinearLayoutManager(context)
            }
            details.observe(viewLifecycleOwner) {
                binding.wordProgress.isVisible = it is State.Loading
                binding.wordDetails.isVisible = it is State.Data
                when (it) {
                    State.Error -> (activity as Host).onWordNotFound(wordText)
                    is State.Data -> listAdapter.submitList(it.word.translations)
                }
            }
            word.observe(viewLifecycleOwner) {
                binding.wordText.text = it
            }
        }
        binding.wordUp.setOnClickListener { nav.up() }
    }
}