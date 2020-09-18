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
import ru.uxapps.vocup.feature.learn.LearnFragment
import ru.uxapps.vocup.feature.learn.model.Exercises
import ru.uxapps.vocup.feature.learn.model.ExercisesImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [LearnModule::class])
internal interface LearnComponent {

    fun inject(f: LearnFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance viewModel: ViewModel, repoProvider: RepoProvider): LearnComponent
    }
}

@Module
internal class LearnModule {

    @ViewModelScope
    @Provides
    fun provideExercises(viewModel: ViewModel, repo: Repo): Exercises =
        ExercisesImp(repo, viewModel.viewModelScope)

}