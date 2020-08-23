package ru.uxapps.vocup.screen.word

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.uxapps.vocup.component.WordDetails
import ru.uxapps.vocup.component.WordDetailsImp
import ru.uxapps.vocup.repo

class WordViewModel(app: Application) : AndroidViewModel(app) {

    private var wordDetails: WordDetails? = null

    fun wordDetails(wordText: String): WordDetails {
        return wordDetails ?: WordDetailsImp(wordText, repo, viewModelScope).also {
            wordDetails = it
        }
    }
}