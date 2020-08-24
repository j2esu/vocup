package ru.uxapps.vocup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.component.Dictionary
import ru.uxapps.vocup.component.DictionaryImp
import ru.uxapps.vocup.feature.ViewModelScope
import ru.uxapps.vocup.screen.dict.DictFragment

@ViewModelScope
@Component(dependencies = [AppComponent::class], modules = [DictModule::class])
interface DictComponent {

    fun inject(f: DictFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance vm: ViewModel, appComponent: AppComponent): DictComponent
    }
}

@Module
class DictModule {

    @ViewModelScope
    @Provides
    fun provideDict(vm: ViewModel, repo: ru.uxapps.vocup.data.Repo): Dictionary = DictionaryImp(repo, vm.viewModelScope)
}