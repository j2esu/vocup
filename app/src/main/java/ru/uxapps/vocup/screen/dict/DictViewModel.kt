package ru.uxapps.vocup.screen.dict

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.uxapps.vocup.component.Dictionary
import ru.uxapps.vocup.component.DictionaryImp
import ru.uxapps.vocup.repo

class DictViewModel(app: Application) : AndroidViewModel(app) {
    val dictionary: Dictionary = DictionaryImp(repo, viewModelScope)
}