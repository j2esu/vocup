package ru.uxapps.vocup.screen.addword

import android.text.InputFilter
import android.view.Gravity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWord.Translation.*
import ru.uxapps.vocup.data.Definition
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.databinding.FragmentAddWordBinding

class AddWordView(
    private val binding: FragmentAddWordBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onSave()
        fun onInput(input: String)
        fun onLangClick(lang: Language)
    }

    init {
        with(binding) {
            addWordSave.setOnClickListener { callback.onSave() }
            addWordInput.editText?.doAfterTextChanged { callback.onInput(it.toString()) }
        }
    }

    fun setTranslation(trans: AddWord.Translation) = with(binding) {
        addWordProgress.isVisible = trans is Progress
        addWordTranslation.isVisible = trans !is Progress
        when (trans) {
            Idle -> addWordTranslation.text = ""
            Fail -> addWordTranslation.setText(R.string.cant_load_translation)
            is Success -> addWordTranslation.text = trans.result.map(Definition::text).toString()
        }
    }

    fun setSaveEnabled(enabled: Boolean) = with(binding) {
        addWordSave.isEnabled = enabled
    }

    fun setMaxWordLength(length: Int) = with(binding) {
        addWordInput.editText?.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    fun setLanguages(languages: List<Language>) = with(binding) {
        addWordLang.text = languages.first().toString()
        addWordLang.setOnClickListener { v ->
            val popupMenu = PopupMenu(root.context, v, Gravity.TOP)
            languages.drop(1).forEach { lang ->
                popupMenu.menu.add(lang.toString()).setOnMenuItemClickListener {
                    callback.onLangClick(lang)
                    true
                }
            }
            popupMenu.show()
        }
    }
}