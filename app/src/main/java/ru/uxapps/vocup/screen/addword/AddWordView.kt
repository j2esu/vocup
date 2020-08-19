package ru.uxapps.vocup.screen.addword

import android.text.InputFilter
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.component.AddWord.DefState.*
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.databinding.FragmentAddWordBinding

class AddWordView(
    private val bind: FragmentAddWordBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onSave(item: DefItem)
        fun onRemove(item: DefItem)
        fun onInput(input: String)
        fun onLangClick(lang: Language)
        fun onUp()
        fun onRetry()
    }

    private val listAdapter = DefListAdapter(callback::onSave, callback::onRemove)

    init {
        bind.addWordInput.apply {
            doAfterTextChanged { callback.onInput(it.toString()) }
            val imm = context.getSystemService<InputMethodManager>()
            imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
        bind.addWordTransList.apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(context)
        }
        bind.addWordToolbar.setNavigationOnClickListener { callback.onUp() }
        bind.addWordRetry.setOnClickListener { callback.onRetry() }
    }

    fun setDefState(state: AddWord.DefState) = with(bind) {
        addWordProgress.isVisible = state is Loading
        addWordLoadError.isVisible = state is Error
        addWordEmptyList.isVisible = state is Data && state.items.isEmpty()
        listAdapter.submitList(if (state is Data) state.items else emptyList())
    }

    fun setMaxWordLength(length: Int) = with(bind) {
        addWordInput.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    fun setLanguages(languages: List<Language>) = with(bind) {
        val langItem = addWordToolbar.menu.findItem(R.id.menu_lang)
        langItem.subMenu.clear()
        languages.forEachIndexed { i, lang ->
            val item = langItem.subMenu.add(lang.nativeName).setOnMenuItemClickListener {
                callback.onLangClick(lang)
                true
            }
            if (i == 0) {
                item.icon = root.context.getDrawable(R.drawable.ic_done)?.apply {
                    setTintList(
                        ContextCompat.getColorStateList(root.context, R.color.target_lang_tint)
                    )
                }
            }
        }
    }
}