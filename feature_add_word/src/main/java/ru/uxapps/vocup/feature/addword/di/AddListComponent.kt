package ru.uxapps.vocup.feature.addword.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.data.RepoProvider
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.feature.addword.AddListFragment
import ru.uxapps.vocup.feature.addword.model.AddList
import ru.uxapps.vocup.feature.addword.model.AddListImp

@ViewModelScope
@Component(dependencies = [RepoProvider::class], modules = [AddListModule::class])
internal interface AddListComponent {

    fun inject(f: AddListFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance vm: ViewModel, @BindsInstance list: List<String>, repoProvider: RepoProvider
        ): AddListComponent
    }
}

@Module
internal class AddListModule {

    @ViewModelScope
    @Provides
    fun provideAddList(vm: ViewModel, repo: Repo, list: List<String>): AddList =
        AddListImp(list, vm.viewModelScope, repo)
}