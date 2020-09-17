package ru.uxapps.vocup.data

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.imp.RepoImp
import ru.uxapps.vocup.data.imp.api.ApiImp
import ru.uxapps.vocup.data.imp.db.FakeDb
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
    fun provideRepo(context: Context): Repo = RepoImp(context, FakeDb, ApiImp(context))

}