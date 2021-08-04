package ru.uxapps.vocup.feature.dictionary.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class DictViewModel(app: Application) : AndroidViewModel(app) {

    val dictComponent = DaggerDictComponent.factory().create(this, app as RepoProvider)

}
