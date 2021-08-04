package ru.uxapps.vocup.feature.addword.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class AddListViewModel(private val app: Application) : AndroidViewModel(app) {

    private var addListComponent: AddListComponent? = null

    fun getAddListComponent(list: List<String>): AddListComponent {
        return addListComponent ?: DaggerAddListComponent.factory().create(this, list, app as RepoProvider)
            .also { addListComponent = it }
    }
}
