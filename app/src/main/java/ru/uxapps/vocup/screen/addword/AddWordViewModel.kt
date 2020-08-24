package ru.uxapps.vocup.screen.addword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.App
import ru.uxapps.vocup.di.DaggerAddWordComponent

class AddWordViewModel(app: Application) : AndroidViewModel(app) {
    val addWordComponent = DaggerAddWordComponent.factory()
        .create(this, getApplication<App>().appComponent)
}