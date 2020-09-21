package ru.uxapps.vocup.feature.learn.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.learn.LearnFragment
import ru.uxapps.vocup.feature.learn.model.GameListModel
import ru.uxapps.vocup.feature.learn.model.GameListModelImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [LearnModule::class])
internal interface LearnComponent {

    fun inject(f: LearnFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance viewModel: ViewModel,
            repoProvider: RepoProvider,
            @BindsInstance context: Context
        ): LearnComponent
    }
}

@Module
internal class LearnModule {

    @ViewModelScope
    @Provides
    fun provideGameList(viewModel: ViewModel, repo: Repo, context: Context): GameListModel =
        GameListModelImp(context, repo, viewModel.viewModelScope)

}