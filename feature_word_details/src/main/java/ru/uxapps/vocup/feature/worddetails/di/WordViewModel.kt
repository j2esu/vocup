package ru.uxapps.vocup.feature.worddetails.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class WordViewModel(private val app: Application) : AndroidViewModel(app) {

    private var wordComponent: WordComponent? = null

    fun getWordComponent(word: String): WordComponent {
        return wordComponent ?: DaggerWordComponent.factory().create(this, word, app as RepoProvider)
            .also { wordComponent = it }
    }
}