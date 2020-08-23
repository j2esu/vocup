package ru.uxapps.vocup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.component.AddWord
import ru.uxapps.vocup.component.AddWordImp
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.screen.addword.AddWordFragment

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [AddWordModule::class])
interface AddWordComponent {

    fun inject(f: AddWordFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance f: AddWordFragment,
            appComponent: AppComponent
        ): AddWordComponent
    }
}

@Module
class AddWordModule {

    @Provides
    fun provideAddWord(f: AddWordFragment, repo: Repo): AddWord {
        val vm = ViewModelProvider(f)[AddWordViewModel::class.java]
        return vm.addWord ?: AddWordImp(repo, vm.viewModelScope).also { vm.addWord = it }
    }
}

class AddWordViewModel : ViewModel() {
    var addWord: AddWord? = null
}