package ru.uxapps.vocup.feature.dictionary.screen.dict

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.feature.dictionary.R
import ru.uxapps.vocup.feature.dictionary.component.Dictionary
import ru.uxapps.vocup.feature.dictionary.databinding.FragmentDictBinding
import ru.uxapps.vocup.feature.router
import ru.uxapps.vocup.util.consume
import javax.inject.Inject

class DictFragment : Fragment(R.layout.fragment_dict) {

    interface Router {
        fun openWord(text: String, target: Fragment)
        fun openAddWord()
    }

    private val vm by viewModels<DictViewModel>()

    @Inject lateinit var dictModel: Dictionary

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        vm.dictComponent.inject(this)
        val v = DictView(FragmentDictBinding.bind(requireView()), object : DictView.Callback {
            override fun onAdd() = router<Router>().openAddWord()
            override fun onSwipe(word: Word) = dictModel.onRemove(word)
            override fun onClick(word: Word) = router<Router>().openWord(word.text, this@DictFragment)
        })
        with(dictModel) {
            words.observe(viewLifecycleOwner, v::setWords)
            loading.observe(viewLifecycleOwner, v::setLoading)
            onWordRemoved.consume(viewLifecycleOwner) {
                v.showRemoveWordUndo { lifecycleScope.launch { it() } }
            }
        }
    }
}