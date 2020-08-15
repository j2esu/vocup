package ru.uxapps.vocup.screen.dict

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.databinding.FragmentDictBinding
import ru.uxapps.vocup.nav
import ru.uxapps.vocup.util.consume

class DictFragment : Fragment(R.layout.fragment_dict) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<DictViewModel>()
        val v = DictView(FragmentDictBinding.bind(view), object : DictView.Callback {
            override fun onAdd() = nav.openAddWord()
            override fun onSwipe(word: Word) = vm.wordList.onRemove(word)
            override fun onClick(word: Word) = nav.openWord(word)
        })
        with(vm.wordList) {
            words.observe(viewLifecycleOwner, v::setWords)
            loading.observe(viewLifecycleOwner, v::setLoading)
            onUndoRemoved.consume(viewLifecycleOwner, v::showRemoveWordUndo)
        }
    }
}