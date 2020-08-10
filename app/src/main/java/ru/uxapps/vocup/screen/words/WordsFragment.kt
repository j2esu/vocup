package ru.uxapps.vocup.screen.words

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentWordsBinding
import ru.uxapps.vocup.nav

class WordsFragment : Fragment(R.layout.fragment_words) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(this)[WordsViewModel::class.java]
        val wordsView = WordsViewImp(FragmentWordsBinding.bind(view),
            nav::openWord, nav::openAddWord)
        vm.words.observe(viewLifecycleOwner, wordsView::setWords)
        vm.loading.observe(viewLifecycleOwner, wordsView::setLoading)
    }
}