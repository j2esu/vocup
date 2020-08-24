package ru.uxapps.vocup.feature.dictionary.screen.dict

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.dictionary.di.DaggerDictComponent

class DictViewModel(app: Application) : AndroidViewModel(app) {

    val dictComponent = DaggerDictComponent.factory().create(this, app as RepoProvider)

}