package ru.uxapps.vocup.screen.addword

import androidx.core.widget.doAfterTextChanged
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.feature.TranslationFeature.State.*

interface AddWordView {

    fun setTranslation(state: TranslationFeature.State?)
    fun setSaveEnabled(enabled: Boolean)

    interface Callback {
        fun onWordInput(text: String)
        fun onSaveClick()
    }
}

class AddWordViewImp(
    private val binding: FragmentAddWordBinding,
    private val callback: AddWordView.Callback
) : AddWordView {

    init {
        binding.addWordSave.setOnClickListener { callback.onSaveClick() }
        binding.addWordInput.doAfterTextChanged { callback.onWordInput(it.toString()) }
    }

    override fun setTranslation(state: TranslationFeature.State?) = with(binding) {
        addWordTranslation.isEnabled = state is Success
        when (state) {
            null -> addWordTranslation.text = ""
            Progress -> addWordTranslation.setText(R.string.loading_translation)
            Fail -> addWordTranslation.setText(R.string.cant_load_translation)
            is Success -> addWordTranslation.text = state.result
        }
    }

    override fun setSaveEnabled(enabled: Boolean) {
        binding.addWordSave.isEnabled = enabled
    }
}