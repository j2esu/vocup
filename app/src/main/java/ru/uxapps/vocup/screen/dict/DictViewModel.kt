package ru.uxapps.vocup.screen.dict

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.uxapps.vocup.component.Dictionary
import ru.uxapps.vocup.component.DictionaryImp
import ru.uxapps.vocup.repo

class DictViewModel : ViewModel() {
    val dictionary: Dictionary = DictionaryImp(repo, viewModelScope)
}