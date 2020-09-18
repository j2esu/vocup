package ru.uxapps.vocup.feature.learn.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class LearnViewModel(app: Application) : AndroidViewModel(app) {

    val learnComponent = DaggerLearnComponent.factory().create(this, app as RepoProvider)

}