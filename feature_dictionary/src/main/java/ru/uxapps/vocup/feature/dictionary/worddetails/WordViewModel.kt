package ru.uxapps.vocup.feature.dictionary.worddetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.dictionary.di.DaggerWordComponent
import ru.uxapps.vocup.feature.dictionary.di.WordComponent

class WordViewModel(private val app: Application) : AndroidViewModel(app) {

    private var wordComponent: WordComponent? = null

    fun getWordComponent(word: String): WordComponent {
        return wordComponent ?: DaggerWordComponent.factory().create(this, word, app as RepoProvider)
            .also { wordComponent = it }
    }
}