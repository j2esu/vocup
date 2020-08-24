package ru.uxapps.vocup.screen.dict

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.component.Dictionary
import ru.uxapps.vocup.databinding.FragmentDictBinding
import ru.uxapps.vocup.screen.word.WordFragment
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.router
import javax.inject.Inject

class DictFragment : Fragment(R.layout.fragment_dict), WordFragment.Target {

    interface Router {
        fun openWord(text: String, target: Fragment)
        fun openAddWord()
    }

    private val vm by viewModels<DictViewModel>()

    @Inject lateinit var dictModel: Dictionary
    private lateinit var dictView: DictView

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.dictComponent.inject(this)
        dictView = DictView(FragmentDictBinding.bind(requireView()), object : DictView.Callback {
            override fun onAdd() = router<Router>().openAddWord()
            override fun onSwipe(word: ru.uxapps.vocup.data.Word) = dictModel.onRemove(word)
            override fun onClick(word: ru.uxapps.vocup.data.Word) = router<Router>().openWord(word.text, this@DictFragment)
        })
        with(dictModel) {
            words.observe(viewLifecycleOwner, dictView::setWords)
            loading.observe(viewLifecycleOwner, dictView::setLoading)
            onWordRemoved.consume(viewLifecycleOwner) {
                dictView.showRemoveWordUndo { dictModel.restoreWord(it) }
            }
        }
    }

    override fun onWordDeleted(word: ru.uxapps.vocup.data.Word) {
        lifecycleScope.launchWhenStarted {
            dictView.showRemoveWordUndo { dictModel.restoreWord(word) }
        }
    }
}