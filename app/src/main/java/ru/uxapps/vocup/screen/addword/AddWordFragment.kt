package ru.uxapps.vocup.screen.addword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.nav

class AddWordFragment : Fragment(R.layout.fragment_add_word) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<AddWordViewModel>()
        val v = AddWordView(FragmentAddWordBinding.bind(view), object : AddWordView.Callback {
            override fun onSave(item: DefItem) = vm.addWord.onSave(item)
            override fun onRemove(item: DefItem) = vm.addWord.onRemove(item)
            override fun onInput(input: String) = vm.addWord.onInput(input)
            override fun onLangClick(lang: Language) = vm.addWord.onChooseLang(lang)
            override fun onUp() = nav.up()
        })
        with(vm.addWord) {
            definitions.observe(viewLifecycleOwner, v::setDefState)
            v.setMaxWordLength(maxWordLength)
            languages.observe(viewLifecycleOwner, v::setLanguages)
        }
    }
}