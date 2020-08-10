package ru.uxapps.vocup.screen.word

import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentWordBinding
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.feature.TranslationFeature.State.*

interface WordView {
    fun setWordText(text: String)
    fun setTranslation(state: TranslationFeature.State)
}

class WordViewImp(
    private val binding: FragmentWordBinding,
    private val onRetryClick: () -> Unit
) : WordView {

    init {
        binding.wordTranslation.setOnClickListener { onRetryClick() }
    }

    override fun setWordText(text: String) {
        binding.wordText.text = text
    }

    override fun setTranslation(state: TranslationFeature.State) = with(binding) {
        wordTranslation.isEnabled = state is Fail
        when (state) {
            is Success -> wordTranslation.text = state.result
            Fail -> wordTranslation.setText(R.string.reload_translation)
            Progress -> wordTranslation.setText(R.string.loading_translation)
        }
    }
}