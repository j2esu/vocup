package ru.uxapps.vocup.feature.addword

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.feature.addword.AddWord.DefItem
import ru.uxapps.vocup.feature.addword.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class AddWordFragment : Fragment(R.layout.fragment_add_word) {

    interface Router {
        fun openWord(text: String)
    }

    private val vm by viewModels<AddWordViewModel>()

    @Inject lateinit var addWordModel: AddWord

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.addWordComponent.inject(this)
        val v = AddWordView(FragmentAddWordBinding.bind(requireView()), object : AddWordView.Callback {
            override fun onOpen(item: DefItem) = host<Router>().openWord(item.text)
            override fun onSave(item: DefItem) = addWordModel.onSave(item)
            override fun onInput(input: String) = addWordModel.onInput(input)
            override fun onLangClick(lang: Language) = addWordModel.onChooseLang(lang)
            override fun onRetry() = addWordModel.onRetry()
            override fun onInputDone(text: String) = addWordModel.onSearch(text)
            override fun onCompClick(text: String) = addWordModel.onSearch(text)
        })
        with(addWordModel) {
            v.setMaxWordLength(maxWordLength)
            languages.observe(viewLifecycleOwner, v::setLanguages)
            state.observe(viewLifecycleOwner, v::setState)
        }
    }
}