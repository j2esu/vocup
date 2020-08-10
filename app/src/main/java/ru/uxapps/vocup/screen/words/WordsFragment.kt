package ru.uxapps.vocup.screen.words

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentWordsBinding
import ru.uxapps.vocup.nav

class WordsFragment : Fragment(R.layout.fragment_words) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm: WordsVm by viewModels<WordsVmImp>()
        val v = WordsViewImp(FragmentWordsBinding.bind(view),
            nav::openWord, nav::openAddWord)
        vm.words.observe(viewLifecycleOwner, v::setWords)
        vm.loading.observe(viewLifecycleOwner, v::setLoading)
    }
}