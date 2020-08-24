package ru.uxapps.vocup

import android.app.Application
import ru.uxapps.vocup.data.DaggerRepoComponent
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.di.DaggerAppComponent

class App : Application(), RepoProvider {

    private val appComponent by lazy {
        DaggerAppComponent.factory().create(DaggerRepoComponent.factory().create(this))
    }

    override fun provideRepo() = appComponent.provideRepo()
}