package ru.uxapps.vocup.feature.addword.view

import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.feature.addword.R
import ru.uxapps.vocup.feature.addword.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.feature.addword.model.AddWord.State
import ru.uxapps.vocup.feature.addword.model.AddWord.State.*
import ru.uxapps.vocup.feature.addword.model.DefItem
import ru.uxapps.vocup.feature.setNavAsBack
import ru.uxapps.vocup.feature.softInput

internal class AddWordView(
    private val bind: FragmentAddWordBinding,
    private val init: Boolean,
    private val callback: Callback
) {

    interface Callback {
        fun onOpen(item: DefItem, srcView: View)
        fun onSave(item: DefItem)
        fun onInput(input: String)
        fun onLangClick(lang: Language)
        fun onRetry()
        fun onInputDone(text: String)
        fun onCompClick(text: String)
    }

    private val langView = LangView(bind.addWordToolbar, callback::onLangClick)
    private val defAdapter = DefListAdapter(callback::onOpen, callback::onSave)

    private var errorSnack: Snackbar? = null
    private var handlingCompletion = false
    private val compAdapter = CompletionAdapter {
        handlingCompletion = true
        bind.addWordInput.text.apply {
            replace(0, length, it)
            callback.onCompClick(this.toString()) // get new input with applied filters
        }
        handlingCompletion = false
    }

    init {
        bind.addWordInput.apply {
            doAfterTextChanged {
                if (!handlingCompletion) {
                    callback.onInput(it.toString())
                }
            }
            if (init) {
                softInput.show(this)
            }
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    callback.onInputDone(v.text.toString())
                    true
                } else {
                    false
                }
            }
        }
        bind.addWordDefList.apply {
            adapter = defAdapter
            layoutManager = LinearLayoutManager(context)
        }
        bind.addWordToolbar.setNavAsBack()
        bind.addWordCompList.apply {
            adapter = compAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        bind.root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {}
            override fun onViewDetachedFromWindow(v: View?) {
                errorSnack?.dismiss()
            }
        })
    }

    fun setState(state: State) {
        bind.addWordProgress.isVisible = state is Loading
        bind.addWordDefList.apply {
            if ((state as? Definitions)?.error == true) {
                if (errorSnack == null) {
                    errorSnack = Snackbar.make(
                        bind.root, R.string.cant_load_translations, Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.retry) { callback.onRetry() }
                    errorSnack?.show()
                }
            } else {
                errorSnack?.dismiss()
                errorSnack = null
            }
            defAdapter.submitList((state as? Definitions)?.items ?: emptyList())
            isInvisible = state !is Definitions // gone delays animations -> see blink on new list shown
        }
        bind.addWordCompList.apply {
            compAdapter.submitList((state as? Completions)?.items ?: emptyList())
            isVisible = state is Completions
        }
    }

    fun setMaxWordLength(length: Int) = with(bind) {
        addWordInput.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    fun setLanguages(languages: List<Language>) = langView.setLanguages(languages)
}