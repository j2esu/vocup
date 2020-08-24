package ru.uxapps.vocup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWordImp
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.screen.addword.AddWordFragment

@ViewModelScope
@Component(dependencies = [AppComponent::class], modules = [AddWordModule::class])
interface AddWordComponent {

    fun inject(f: AddWordFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance vm: ViewModel, appComponent: AppComponent): AddWordComponent
    }
}

@Module
class AddWordModule {

    @ViewModelScope
    @Provides
    fun provideAddWord(vm: ViewModel, repo: Repo): AddWord = AddWordImp(repo, vm.viewModelScope)
}