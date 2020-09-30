package ru.uxapps.vocup.feature.explore.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class ExploreViewModel(app: Application) : AndroidViewModel(app) {

    val exploreComponent = DaggerExploreComponent.factory().create(this, app as RepoProvider)

}