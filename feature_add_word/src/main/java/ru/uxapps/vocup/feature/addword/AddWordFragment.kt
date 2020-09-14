package ru.uxapps.vocup.feature.addword

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.addword.databinding.FragmentAddWordBinding
import ru.uxapps.vocup.feature.addword.di.AddWordViewModel
import ru.uxapps.vocup.feature.addword.model.AddWord
import ru.uxapps.vocup.feature.addword.model.AddWord.DefItem
import ru.uxapps.vocup.feature.addword.view.AddWordView
import ru.uxapps.vocup.feature.awaitValue
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class AddWordFragment : BaseFragment(R.layout.fragment_add_word) {

    interface Router {
        fun openWord(text: String, srcView: View)
    }

    private val vm by viewModels<AddWordViewModel>()

    @Inject internal lateinit var addWordModel: AddWord

    override fun onViewReady(view: View, init: Boolean) {
        vm.addWordComponent.inject(this)
        val v = AddWordView(FragmentAddWordBinding.bind(view), init, object : AddWordView.Callback {
            override fun onOpen(item: DefItem, srcView: View) = host<Router>().openWord(item.text, srcView)
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
        postponeUntil {
            addWordModel.state.awaitValue()
        }
    }

    override fun getViewsSavedForTransition() = intArrayOf(R.id.add_word_def_list)
}