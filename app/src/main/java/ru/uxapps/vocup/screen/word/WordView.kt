package ru.uxapps.vocup.screen.word

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.WordDetails
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentWordBinding

class WordView(
        private val binding: FragmentWordBinding,
        private val callback: Callback
) {

    private val transAdapter = TransListAdapter()
    private var word: Word? = null

    init {
        binding.wordToolbar.apply {
            setNavigationOnClickListener { callback.onUp() }
            menu.findItem(R.id.menu_word_del).setOnMenuItemClickListener {
                word?.let { callback.onDelete(it) }
                true
            }
        }
        binding.wordTransList.apply {
            adapter = transAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun setDetails(state: WordDetails.State) = with(binding) {
        wordProgress.isVisible = state is WordDetails.State.Loading
        wordDetails.isVisible = state is WordDetails.State.Data
        when (state) {
            WordDetails.State.Error -> callback.onWordNotFound()
            is WordDetails.State.Data -> {
                transAdapter.submitList(state.word.translations)
                word = state.word
            }
        }
    }

    fun setWordText(text: String) = with(binding) {
        wordText.text = text
    }

    interface Callback {
        fun onWordNotFound()
        fun onUp()
        fun onDelete(word: Word)
    }
}