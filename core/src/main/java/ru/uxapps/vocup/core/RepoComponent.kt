package ru.uxapps.vocup.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.core.data.FakeApi
import ru.uxapps.vocup.core.data.FakeDb
import ru.uxapps.vocup.core.data.Repo
import ru.uxapps.vocup.core.data.RepoImp
import javax.inject.Singleton

@Singleton
@Component(modules = [RepoModule::class])
interface RepoComponent : RepoProvider {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): RepoComponent
    }
}

@Module
class RepoModule {

    @Singleton
    @Provides
    fun provideRepo(context: Context): Repo = RepoImp(context, FakeDb, FakeApi)
}