package ru.uxapps.vocup.feature.dictionary.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.dictionary.WordFragment
import ru.uxapps.vocup.feature.dictionary.model.WordDetails
import ru.uxapps.vocup.feature.dictionary.model.WordDetailsImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [WordModule::class])
internal interface WordComponent {

    fun inject(f: WordFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance vm: ViewModel, @BindsInstance wordId: Long, repoProvider: RepoProvider
        ): WordComponent
    }
}

@Module
internal class WordModule {

    @ViewModelScope
    @Provides
    fun provideDetails(vm: ViewModel, wordId: Long, repo: Repo): WordDetails =
        WordDetailsImp(wordId, repo, vm.viewModelScope)
}
