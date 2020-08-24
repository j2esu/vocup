package ru.uxapps.vocup.feature.dictionary.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.dictionary.dict.DictFragment
import ru.uxapps.vocup.feature.dictionary.model.Dictionary
import ru.uxapps.vocup.feature.dictionary.model.DictionaryImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [DictModule::class])
interface DictComponent {

    fun inject(f: DictFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance vm: ViewModel, repoProvider: RepoProvider): DictComponent
    }
}

@Module
class DictModule {

    @ViewModelScope
    @Provides
    fun provideDict(vm: ViewModel, repo: Repo): Dictionary = DictionaryImp(repo, vm.viewModelScope)
}