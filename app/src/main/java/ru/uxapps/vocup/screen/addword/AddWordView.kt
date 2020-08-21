package ru.uxapps.vocup.screen.addword

import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.component.AddWord.DefState
import ru.uxapps.vocup.component.AddWord.DefState.Data
import ru.uxapps.vocup.component.AddWord.DefState.Loading
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.util.setNavAsBack

class AddWordView(
    private val bind: FragmentAddWordBinding,
    private val callback: Callback
) {

    interface Callback {
        fun onOpen(item: DefItem)
        fun onSave(item: DefItem)
        fun onInput(input: String)
        fun onLangClick(lang: Language)
        fun onRetry()
    }

    private val listAdapter = DefListAdapter { item ->
        if (!item.saved || item.trans?.any { !it.second } == true) {
            callback.onSave(item)
        } else {
            callback.onOpen(item)
        }
    }
    private val errorSnack = Snackbar.make(bind.root, R.string.cant_load_translations, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.retry) { callback.onRetry() }
        .also {
            bind.root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {}
                override fun onViewDetachedFromWindow(v: View?) {
                    it.dismiss()
                }
            })
        }

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
        bind.addWordToolbar.setNavAsBack()
    }

    fun setDefState(state: DefState) {
        bind.addWordProgress.isVisible = state is Loading
        listAdapter.apply {
            if ((state as? Data)?.error == true) {
                errorSnack.show()
            } else {
                errorSnack.dismiss()
            }
            submitList(if (state is Data) state.items else emptyList())
        }
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

    fun showUndoDeleteWord(undo: () -> Unit) {
        Snackbar.make(bind.root, R.string.word_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { undo() }
            .show()
    }
}