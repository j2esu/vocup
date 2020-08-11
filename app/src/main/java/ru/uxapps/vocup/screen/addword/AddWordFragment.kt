package ru.uxapps.vocup.screen.addword

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
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
            binding.addWordSave.setOnClickListener {
                if (!binding.addWordSave.isOrWillBeHidden) {
                    onSave()
                }
            }
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
                if (it) binding.addWordSave.show() else binding.addWordSave.hide()
            }
            languages.observe(viewLifecycleOwner) { languages ->
                binding.addWordLang.text = languages.first().toString()
                binding.addWordLang.setOnClickListener { v ->
                    val popupMenu = PopupMenu(requireContext(), v, Gravity.TOP)
                    languages.drop(1).forEach { lang ->
                        popupMenu.menu.add(lang.toString()).setOnMenuItemClickListener {
                            chooseLang(lang)
                            true
                        }
                    }
                    popupMenu.show()
                }
            }
            onWordAdded.consume(viewLifecycleOwner, (activity as Host)::onWordAdded)
        }
    }
}