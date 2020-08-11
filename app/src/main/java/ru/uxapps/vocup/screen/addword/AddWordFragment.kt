package ru.uxapps.vocup.screen.addword

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.Translation
import ru.uxapps.vocup.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.util.consume

class AddWordFragment : Fragment(R.layout.fragment_add_word) {

    interface Host {
        fun onWordAdded(text: String)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<AddWordViewModel>()
        val binding = FragmentAddWordBinding.bind(view)
        with(vm.addWord) {
            binding.addWordSave.setOnClickListener { onSave() }
            binding.addWordInput.doAfterTextChanged { onWordInput(it.toString()) }
            translation.observe(viewLifecycleOwner) {
                binding.addWordTranslation.isEnabled = it is Translation.State.Success
                when (it) {
                    null -> binding.addWordTranslation.text = ""
                    Translation.State.Progress -> binding.addWordTranslation.setText(R.string.loading_translation)
                    Translation.State.Fail -> binding.addWordTranslation.setText(R.string.cant_load_translation)
                    is Translation.State.Success -> binding.addWordTranslation.text = it.result
                }
            }
            saveEnabled.observe(viewLifecycleOwner) {
                binding.addWordSave.isEnabled = it
            }
            languages.observe(viewLifecycleOwner) {
                binding.addWordLang.text = it.first().toString()
            }
            onWordAdded.consume(viewLifecycleOwner, (activity as Host)::onWordAdded)
        }
    }
}