package ru.uxapps.vocup.screen.addword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.AddWord.DefItem
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.screen.word.WordFragment
import ru.uxapps.vocup.util.router

class AddWordFragment : Fragment(R.layout.fragment_add_word), WordFragment.Target {

    interface Router {
        fun openWord(text: String, target: Fragment)
    }

    private val vm by viewModels<AddWordViewModel>()
    private val addWordModel by lazy { vm.addWord }
    private lateinit var addWordView: AddWordView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addWordView = AddWordView(FragmentAddWordBinding.bind(view), object : AddWordView.Callback {
            override fun onOpen(item: DefItem) = router<Router>().openWord(item.text, this@AddWordFragment)
            override fun onSave(item: DefItem) = addWordModel.onSave(item)
            override fun onInput(input: String) = addWordModel.onInput(input)
            override fun onLangClick(lang: Language) = addWordModel.onChooseLang(lang)
            override fun onRetry() = addWordModel.onRetry()
        })
        with(addWordModel) {
            addWordView.setMaxWordLength(maxWordLength)
            languages.observe(viewLifecycleOwner, addWordView::setLanguages)
            definitions.observe(viewLifecycleOwner, addWordView::setDefState)
        }
    }

    override fun onWordDeleted(word: Word) {
        lifecycleScope.launchWhenStarted {
            addWordView.showUndoDeleteWord { addWordModel.onRestoreWord(word) }
        }
    }
}