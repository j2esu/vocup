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
import ru.uxapps.vocup.feature.TranslationFeature
import ru.uxapps.vocup.feature.TranslationFeature.State.*
import ru.uxapps.vocup.util.input

class AddWordFragment : Fragment(R.layout.fragment_add_word) {

    interface Host {
        fun onWordAdded(text: String)
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
                val wordText = inputEt.text.toString()
                repo.addWord(wordText)
                (activity as Host).onWordAdded(wordText)
            }
        }
        // init translation
        val trans = TranslationFeature(repo)
        val transTv = view.findViewById<TextView>(R.id.addWordTranslation)
        val transFlow = inputEt.input(lifecycleScope).consumeAsFlow()
            .map { it.trim() }
            .distinctUntilChanged()
            .debounce(400)
            .flatMapLatest {
                if (it.length > 1) trans.getTranslation(it) else flowOf(null)
            }
        lifecycleScope.launchWhenStarted {
            transFlow.collect {
                transTv.isEnabled = it is Success
                when (it) {
                    null -> transTv.text = ""
                    Progress -> transTv.setText(R.string.loading_translation)
                    Fail -> transTv.setText(R.string.cant_load_translation)
                    is Success -> transTv.text = it.result
                }
            }
        }
    }
}