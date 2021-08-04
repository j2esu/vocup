package ru.uxapps.vocup.feature.learn.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.learn.GameFragment
import ru.uxapps.vocup.feature.learn.model.GameModel
import ru.uxapps.vocup.feature.learn.model.GameModelImp

@ViewModelScope
@Component(modules = [GameModule::class], dependencies = [RepoProvider::class])
internal interface GameComponent {

    fun inject(f: GameFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance viewModel: ViewModel, @BindsInstance gameId: Int, repoProvider: RepoProvider
        ): GameComponent
    }
}

@Module
internal class GameModule {

    @ViewModelScope
    @Provides
    fun provideGame(viewModel: ViewModel, gameId: Int, repo: Repo): GameModel =
        GameModelImp(gameId, viewModel.viewModelScope, repo)

}
