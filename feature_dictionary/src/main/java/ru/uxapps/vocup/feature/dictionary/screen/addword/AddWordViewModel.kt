package ru.uxapps.vocup.feature.dictionary.screen.addword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.dictionary.di.DaggerAddWordComponent

class AddWordViewModel(app: Application) : AndroidViewModel(app) {

    val addWordComponent = DaggerAddWordComponent.factory().create(this, app as RepoProvider)

}