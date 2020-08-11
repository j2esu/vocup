package ru.uxapps.vocup.screen.words

import androidx.lifecycle.ViewModel
import ru.uxapps.vocup.component.WordList
import ru.uxapps.vocup.component.WordListImp
import ru.uxapps.vocup.repo

class WordsViewModel : ViewModel() {
    val wordList: WordList = WordListImp(repo)
}