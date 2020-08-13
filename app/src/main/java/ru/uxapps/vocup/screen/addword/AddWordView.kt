package ru.uxapps.vocup.screen.addword

import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWord.TransItem
import ru.uxapps.vocup.component.AddWord.TransState.*
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.databinding.FragmentAddWordBinding

class AddWordView(
    private val binding: FragmentAddWordBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onSave(item: TransItem)
        fun onRemove(item: TransItem)
        fun onInput(input: String)
        fun onLangClick(lang: Language)
        fun onUp()
    }

    private val adapter = TransListAdapter(callback::onSave, callback::onRemove)

    init {
        with(binding) {
            addWordInput.doAfterTextChanged { callback.onInput(it.toString()) }
            addWortTransList.adapter = adapter
            addWortTransList.layoutManager = LinearLayoutManager(root.context)
            addWordToolbar.setNavigationOnClickListener { callback.onUp() }
            val imm = root.context.getSystemService<InputMethodManager>()
            imm?.showSoftInput(addWordInput, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun setTranslation(state: AddWord.TransState) = with(binding) {
        addWordProgress.isVisible = state is Progress
        addWordLoadError.isVisible = state is Fail
        addWordEmptyList.isVisible = state is Success && state.result.isEmpty()
        adapter.submitList(if (state is Success) state.result else emptyList())
    }

    fun setMaxWordLength(length: Int) = with(binding) {
        addWordInput.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    fun setLanguages(languages: List<Language>) = with(binding) {
        val langItem = addWordToolbar.menu.findItem(R.id.menu_lang)
        langItem.subMenu.clear()
        languages.forEachIndexed { i, lang ->
            val item = langItem.subMenu.add(lang.nativeName).setOnMenuItemClickListener {
                callback.onLangClick(lang)
                true
            }
            if (i == 0) {
                item.setIcon(R.drawable.ic_done)
            }
        }
    }
}