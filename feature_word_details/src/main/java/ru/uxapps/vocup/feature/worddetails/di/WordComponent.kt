package ru.uxapps.vocup.feature.worddetails.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.worddetails.WordDetails
import ru.uxapps.vocup.feature.worddetails.WordDetailsImp
import ru.uxapps.vocup.feature.worddetails.WordFragment

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [WordModule::class])
interface WordComponent {

    fun inject(f: WordFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance vm: ViewModel, @BindsInstance word: String, repoProvider: RepoProvider
        ): WordComponent
    }
}

@Module
class WordModule {

    @ViewModelScope
    @Provides
    fun provideDetails(vm: ViewModel, word: String, repo: Repo): WordDetails =
        WordDetailsImp(word, repo, vm.viewModelScope)
}