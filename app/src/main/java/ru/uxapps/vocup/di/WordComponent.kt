package ru.uxapps.vocup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.component.WordDetails
import ru.uxapps.vocup.component.WordDetailsImp
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.screen.word.WordFragment

@ViewModelScope
@Component(dependencies = [AppComponent::class], modules = [WordModule::class])
interface WordComponent {

    fun inject(f: WordFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance vm: ViewModel, @BindsInstance word: String, appComponent: AppComponent
        ): WordComponent
    }
}

@Module
class WordModule {

    @ViewModelScope
    @Provides
    fun provideDetails(vm: ViewModel, word: String, repo: ru.uxapps.vocup.data.Repo): WordDetails =
        WordDetailsImp(word, repo, vm.viewModelScope)
}