package ru.uxapps.vocup.screen.word

import androidx.lifecycle.ViewModel
import ru.uxapps.vocup.component.WordDetails
import ru.uxapps.vocup.component.WordDetailsImp
import ru.uxapps.vocup.repo

class WordViewModel : ViewModel() {

    private var wordDetails: WordDetails? = null

    fun wordDetails(wordText: String): WordDetails {
        return wordDetails ?: WordDetailsImp(wordText, repo).also { wordDetails = it }
    }
}