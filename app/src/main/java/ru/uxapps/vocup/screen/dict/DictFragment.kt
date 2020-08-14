package ru.uxapps.vocup.screen.dict

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentDictBinding
import ru.uxapps.vocup.nav

class DictFragment : Fragment(R.layout.fragment_dict) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm by viewModels<DictViewModel>()
        val binding = FragmentDictBinding.bind(view)
        with(vm.wordList) {
            val adapter = DictListAdapter(nav::openWord)
            binding.dictList.adapter = adapter
            binding.dictList.layoutManager = LinearLayoutManager(context)
            binding.dictList.addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))
            binding.dictAdd.setOnClickListener { nav.openAddWord() }
            words.observe(viewLifecycleOwner) {
                adapter.submitList(it)
                binding.dictEmpty.isVisible = it.isEmpty()
            }
            loading.observe(viewLifecycleOwner) {
                binding.dictProgress.isVisible = it
            }
        }
    }
}