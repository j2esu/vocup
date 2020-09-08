package ru.uxapps.vocup.feature.dictionary

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.delayTransition
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentDictBinding
import ru.uxapps.vocup.feature.dictionary.di.DictViewModel
import ru.uxapps.vocup.feature.dictionary.model.Dictionary
import ru.uxapps.vocup.feature.dictionary.view.DictView
import ru.uxapps.vocup.util.consume
import ru.uxapps.vocup.util.host
import javax.inject.Inject

class DictFragment : Fragment(R.layout.fragment_dict) {

    interface Router {
        fun openWord(text: String, srcItem: View)
        fun openAddWord()
    }

    private val vm by viewModels<DictViewModel>()

    @Inject internal lateinit var dictModel: Dictionary

    private val viewState = SparseArray<Parcelable>()

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.dictComponent.inject(this)
        val v = DictView(FragmentDictBinding.bind(requireView()), object : DictView.Callback {
            override fun onAdd() = host<Router>().openAddWord()
            override fun onSwipe(word: Word) = dictModel.onRemove(word)
            override fun onClick(word: Word, srcItem: View) = host<Router>().openWord(word.text, srcItem)
        })
        with(dictModel) {
            words.observe(viewLifecycleOwner, v::setWords)
            loading.observe(viewLifecycleOwner, v::setLoading)
            onWordRemoved.consume(viewLifecycleOwner) {
                v.showRemoveWordUndo { lifecycleScope.launch { it() } }
            }
        }
        delayTransition(dictModel.words) { view?.restoreHierarchyState(viewState) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.saveHierarchyState(viewState)
    }
}