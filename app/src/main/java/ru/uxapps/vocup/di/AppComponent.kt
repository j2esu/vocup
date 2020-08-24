package ru.uxapps.vocup.di

import dagger.Component
import ru.uxapps.vocup.data.RepoProvider
import javax.inject.Singleton

@Singleton
@Component(dependencies = [ru.uxapps.vocup.data.RepoProvider::class])
interface AppComponent : ru.uxapps.vocup.data.RepoProvider {

    @Component.Factory
    interface Factory {
        fun create(repoProvider: ru.uxapps.vocup.data.RepoProvider): AppComponent
    }
}