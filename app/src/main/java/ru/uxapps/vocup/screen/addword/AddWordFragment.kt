package ru.uxapps.vocup.screen.addword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import ru.uxapps.vocup.R
import ru.uxapps.vocup.databinding.FragmentAddWordBinding

class AddWordFragment : Fragment(R.layout.fragment_add_word) {

    interface Host {
        fun onWordAdded(text: String)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm: AddWordVm by viewModels<AddWordVmImp>()
        val v = AddWordViewImp(FragmentAddWordBinding.bind(view),
            object : AddWordView.Callback {
                override fun onWordInput(text: String) = vm.onWordInput(text)
                override fun onSaveClick() = vm.onSave()
            })
        vm.translation.observe(viewLifecycleOwner, v::setTranslation)
        vm.saveEnabled.observe(viewLifecycleOwner, v::setSaveEnabled)
        vm.languages.observe(viewLifecycleOwner, v::setLanguages)
        lifecycleScope.launchWhenStarted {
            for (event in vm.onWordAdded) {
                (activity as Host).onWordAdded(event)
            }
        }
    }
}