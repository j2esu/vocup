package ru.uxapps.vocup.di

import dagger.Component
import ru.uxapps.vocup.core.data.RepoProvider
import javax.inject.Singleton

@Singleton
@Component(dependencies = [RepoProvider::class])
interface AppComponent : RepoProvider {

    @Component.Factory
    interface Factory {
        fun create(repoProvider: RepoProvider): AppComponent
    }
}