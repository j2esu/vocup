package ru.uxapps.vocup.data

import android.content.Context
import android.widget.Toast
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.imp.BuildConfig
import ru.uxapps.vocup.data.imp.RepoImp
import ru.uxapps.vocup.data.imp.api.ApiImp
import ru.uxapps.vocup.data.imp.api.stub.ApiStub
import ru.uxapps.vocup.data.imp.db.DbImp
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
    fun provideRepo(context: Context): Repo {
        val api = if (
            BuildConfig.DICTIONARY_API_KEY != "null"
            && BuildConfig.DICTIONARY_API_REGION != "null"
            && BuildConfig.PREDICTOR_API_KEY != "null"
        ) {
            ApiImp(context)
        } else {
            Toast.makeText(context, "No API keys found, using stub. See README", Toast.LENGTH_LONG).show()
            ApiStub()
        }
        return RepoImp(context, DbImp(context), api)
    }
}