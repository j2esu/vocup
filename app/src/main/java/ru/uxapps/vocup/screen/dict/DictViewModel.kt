package ru.uxapps.vocup.screen.dict

import androidx.lifecycle.ViewModel
import ru.uxapps.vocup.component.WordList
import ru.uxapps.vocup.component.WordListImp
import ru.uxapps.vocup.repo

class DictViewModel : ViewModel() {
    val wordList: WordList = WordListImp(repo)
}