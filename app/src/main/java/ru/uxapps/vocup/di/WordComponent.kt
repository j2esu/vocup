package ru.uxapps.vocup.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import ru.uxapps.vocup.component.WordDetails
import ru.uxapps.vocup.component.WordDetailsImp
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.screen.word.WordFragment

@FragmentScope
@Component(dependencies = [AppComponent::class], modules = [WordModule::class])
interface WordComponent {

    fun inject(f: WordFragment)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance f: WordFragment,
            @BindsInstance word: String,
            appComponent: AppComponent
        ): WordComponent
    }
}

@Module
class WordModule {

    @Provides
    fun provideDetails(f: WordFragment, word: String, repo: Repo): WordDetails {
        val vm = ViewModelProvider(f)[WordViewModel::class.java]
        return vm.wordDetails ?: WordDetailsImp(word, repo, vm.viewModelScope).also { vm.wordDetails = it }
    }
}

class WordViewModel : ViewModel() {
    var wordDetails: WordDetails? = null
}