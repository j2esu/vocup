package ru.uxapps.vocup.screen.addword

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.text.trimmedLength
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.repo
import ru.uxapps.vocup.screen.addword.AddWordFragment.TranslationResult.*
import ru.uxapps.vocup.util.input
import java.io.IOException

class AddWordFragment : Fragment(R.layout.fragment_add_word) {

    interface Host {
        fun closeAddWord()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val inputEt = view.findViewById<EditText>(R.id.addWordInput)
        // init save button
        val saveBtn = view.findViewById<View>(R.id.addWordSave)
        inputEt.doAfterTextChanged {
            saveBtn.isEnabled = it != null && it.trimmedLength() > 1
        }
        saveBtn.setOnClickListener {
            saveBtn.isEnabled = false
            lifecycleScope.launch {
                repo.addWord(inputEt.text.toString())
                (activity as Host).closeAddWord()
            }
        }
        // init translation
        val transTv = view.findViewById<TextView>(R.id.addWordTranslation)
        lifecycleScope.launchWhenStarted {
            inputEt.input(lifecycleScope).consumeAsFlow()
                .map { it.trim() }
                .distinctUntilChanged()
                .debounce(400)
                .flatMapLatest {
                    if (it.length > 1) {
                        flow {
                            emit(Progress)
                            val trans = try {
                                repo.getTranslation(it)
                            } catch (e: IOException) {
                                null
                            }
                            emit(if (trans != null) Success(trans) else Fail)
                        }
                    } else {
                        flowOf(Empty)
                    }
                }
                .collect {
                    transTv.isEnabled = it is Success
                    when (it) {
                        Empty -> transTv.text = ""
                        Progress -> transTv.setText(R.string.loading_translation)
                        Fail -> transTv.setText(R.string.cant_load_translation)
                        is Success -> transTv.text = it.result
                    }
                }
        }
    }

    private sealed class TranslationResult {
        object Empty : TranslationResult()
        object Progress : TranslationResult()
        object Fail : TranslationResult()
        data class Success(val result: String) : TranslationResult()
    }
}