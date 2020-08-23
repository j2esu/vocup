package ru.uxapps.vocup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.component.Dictionary
import ru.uxapps.vocup.component.DictionaryImp
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.screen.dict.DictFragment
import ru.uxapps.vocup.util.router

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [DictModule::class])
interface DictComponent {

    fun inject(f: DictFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance f: DictFragment, appComponent: AppComponent): DictComponent
    }
}

@Module
class DictModule {

    @Provides
    fun provideDict(vm: DictViewModel): Dictionary = vm.dictionary!!

    @Provides
    fun provideRouter(f: DictFragment): DictFragment.Router = f.router()

    @Provides
    fun provideViewModel(f: DictFragment, repo: Repo) = ViewModelProvider(f)[DictViewModel::class.java].apply {
        if (dictionary == null) {
            dictionary = DictionaryImp(repo, viewModelScope)
        }
    }
}

class DictViewModel : ViewModel() {
    var dictionary: Dictionary? = null
}