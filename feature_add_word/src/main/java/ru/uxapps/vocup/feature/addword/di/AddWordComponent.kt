package ru.uxapps.vocup.feature.addword.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.addword.AddWord
import ru.uxapps.vocup.feature.addword.AddWordFragment
import ru.uxapps.vocup.feature.addword.AddWordImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [AddWordModule::class])
interface AddWordComponent {

    fun inject(f: AddWordFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance vm: ViewModel, repoProvider: RepoProvider): AddWordComponent
    }
}

@Module
class AddWordModule {

    @ViewModelScope
    @Provides
    fun provideAddWord(vm: ViewModel, repo: Repo): AddWord =
        AddWordImp(repo, vm.viewModelScope)
}