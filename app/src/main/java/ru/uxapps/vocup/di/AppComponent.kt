package ru.uxapps.vocup.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.FakeApi
import ru.uxapps.vocup.data.FakeDb
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.RepoImp
import javax.inject.Singleton

@Singleton
@Component(modules = [RepoModule::class])
interface AppComponent {

    fun provideRepo(): Repo

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}

@Module
class RepoModule {

    @Singleton
    @Provides
    fun provideRepo(context: Context): Repo = RepoImp(context, FakeDb, FakeApi)

}