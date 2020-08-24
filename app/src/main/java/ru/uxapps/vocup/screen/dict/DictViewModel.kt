package ru.uxapps.vocup.screen.dict

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.App
import ru.uxapps.vocup.di.DaggerDictComponent

class DictViewModel(app: Application) : AndroidViewModel(app) {
    val dictComponent = DaggerDictComponent.factory()
        .create(this, getApplication<App>().appComponent)
}