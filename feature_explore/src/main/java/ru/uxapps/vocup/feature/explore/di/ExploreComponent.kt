package ru.uxapps.vocup.feature.explore.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.explore.ExploreFragment
import ru.uxapps.vocup.feature.explore.model.Explore
import ru.uxapps.vocup.feature.explore.model.ExploreImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [ExploreModule::class])
internal interface ExploreComponent {

    fun inject(f: ExploreFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance vm: ViewModel, repoProvider: RepoProvider): ExploreComponent
    }
}

@Module
internal class ExploreModule {

    @ViewModelScope
    @Provides
    fun provideExplore(vm: ViewModel, repo: Repo): Explore = ExploreImp(vm.viewModelScope, repo)
}