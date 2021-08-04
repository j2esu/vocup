package ru.uxapps.vocup.feature.addword.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class AddWordViewModel(app: Application) : AndroidViewModel(app) {

    val addWordComponent = DaggerAddWordComponent.factory().create(this, app as RepoProvider)

}
