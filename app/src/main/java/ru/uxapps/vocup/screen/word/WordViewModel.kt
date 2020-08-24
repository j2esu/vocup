package ru.uxapps.vocup.screen.word

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.App
import ru.uxapps.vocup.di.DaggerWordComponent
import ru.uxapps.vocup.di.WordComponent

class WordViewModel(app: Application) : AndroidViewModel(app) {

    private var wordComponent: WordComponent? = null

    fun getWordComponent(word: String): WordComponent {
        if (wordComponent == null) {
            wordComponent = DaggerWordComponent.factory()
                .create(this, word, getApplication<App>().appComponent)
        }
        return wordComponent!!
    }
}