package ru.uxapps.vocup.screen.addword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWordImp
import ru.uxapps.vocup.repo

class AddWordViewModel : ViewModel() {
    val addWord: AddWord = AddWordImp(repo, viewModelScope)
}