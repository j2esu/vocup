package ru.uxapps.vocup.feature.dictionary.screen.addword

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.feature.dictionary.R
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.feature.dictionary.component.AddWord
import ru.uxapps.vocup.feature.dictionary.component.AddWord.DefItem
import ru.uxapps.vocup.feature.dictionary.screen.word.WordFragment
import ru.uxapps.vocup.feature.router
import javax.inject.Inject

class AddWordFragment : Fragment(R.layout.fragment_add_word), WordFragment.Target {

    interface Router {
        fun openWord(text: String, target: Fragment)
    }

    private val vm by viewModels<AddWordViewModel>()

    @Inject lateinit var addWordModel: AddWord
    private lateinit var addWordView: AddWordView

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.addWordComponent.inject(this)
        addWordView = AddWordView(FragmentAddWordBinding.bind(requireView()), object : AddWordView.Callback {
            override fun onOpen(item: DefItem) = router<Router>().openWord(item.text, this@AddWordFragment)
            override fun onSave(item: DefItem) = addWordModel.onSave(item)
            override fun onInput(input: String) = addWordModel.onInput(input)
            override fun onLangClick(lang: Language) = addWordModel.onChooseLang(lang)
            override fun onRetry() = addWordModel.onRetry()
            override fun onInputDone(text: String) = addWordModel.onSearch(text)
            override fun onCompClick(text: String) = addWordModel.onSearch(text)
        })
        with(addWordModel) {
            addWordView.setMaxWordLength(maxWordLength)
            languages.observe(viewLifecycleOwner, addWordView::setLanguages)
            state.observe(viewLifecycleOwner, addWordView::setState)
        }
    }

    override fun onWordDeleted(word: ru.uxapps.vocup.data.Word) {
        lifecycleScope.launchWhenStarted {
            addWordView.showUndoDeleteWord { addWordModel.onRestoreWord(word) }
        }
    }
}