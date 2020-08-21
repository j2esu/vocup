package ru.uxapps.vocup.screen.dict

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentDictBinding
import ru.uxapps.vocup.screen.word.WordFragment
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.router

class DictFragment : Fragment(R.layout.fragment_dict), WordFragment.Target {

    interface Router {
        fun openWord(text: String, target: Fragment)
        fun openAddWord()
    }

    private val vm by viewModels<DictViewModel>()
    private val dictModel by lazy { vm.dictionary }
    private lateinit var dictView: DictView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dictView = DictView(FragmentDictBinding.bind(view), object : DictView.Callback {
            override fun onAdd() = router<Router>().openAddWord()
            override fun onSwipe(word: Word) = dictModel.onRemove(word)
            override fun onClick(word: Word) = router<Router>().openWord(word.text, this@DictFragment)
        })
        with(dictModel) {
            words.observe(viewLifecycleOwner, dictView::setWords)
            loading.observe(viewLifecycleOwner, dictView::setLoading)
            onUndoRemoved.consume(viewLifecycleOwner, dictView::showRemoveWordUndo)
        }
    }

    override fun onWordDeleted(word: Word) {
        lifecycleScope.launchWhenStarted {
            dictView.showRemoveWordUndo { dictModel.restoreWord(word) }
        }
    }
}