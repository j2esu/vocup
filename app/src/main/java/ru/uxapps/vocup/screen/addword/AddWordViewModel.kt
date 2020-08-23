package ru.uxapps.vocup.screen.addword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWordImp
import ru.uxapps.vocup.repo

class AddWordViewModel(app: Application) : AndroidViewModel(app) {
    val addWord: AddWord = AddWordImp(repo, viewModelScope)
}