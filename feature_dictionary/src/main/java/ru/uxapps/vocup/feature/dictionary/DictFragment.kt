package ru.uxapps.vocup.feature.dictionary

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.BaseFragment
import ru.uxapps.vocup.feature.awaitValue
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentDictBinding
import ru.uxapps.vocup.feature.dictionary.di.DictViewModel
import ru.uxapps.vocup.feature.dictionary.model.Dictionary
import ru.uxapps.vocup.feature.dictionary.view.DictView
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class DictFragment : BaseFragment(R.layout.fragment_dict) {

    interface Router {
        fun openWord(wordId: Long, srcView: View)
        fun openAddWord(srcView: View)
    }

    private val vm by viewModels<DictViewModel>()

    @Inject
    internal lateinit var dictModel: Dictionary

    override fun onViewReady(view: View, init: Boolean) {
        vm.dictComponent.inject(this)
        val v = DictView(FragmentDictBinding.bind(view), object : DictView.Callback {
            override fun onAdd(srcView: View) = host<Router>().openAddWord(srcView)
            override fun onSwipe(word: Word) = dictModel.onRemove(word)
            override fun onClick(word: Word, srcView: View) = host<Router>().openWord(word.id, srcView)
        })
        with(dictModel) {
            words.observe(viewLifecycleOwner, v::setWords)
            loading.observe(viewLifecycleOwner, v::setLoading)
            onWordRemoved.consume(viewLifecycleOwner) {
                v.showRemoveWordUndo { lifecycleScope.launch { it() } }
            }
        }
        postponeUntil {
            dictModel.words.awaitValue()
        }
    }

    override fun getViewsSavedForTransition() = intArrayOf(R.id.dict_list)
}
