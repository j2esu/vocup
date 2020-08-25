package ru.uxapps.vocup.feature.addword

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.addword.di.DaggerAddWordComponent

class AddWordViewModel(app: Application) : AndroidViewModel(app) {

    val addWordComponent = DaggerAddWordComponent.factory().create(this, app as RepoProvider)

}