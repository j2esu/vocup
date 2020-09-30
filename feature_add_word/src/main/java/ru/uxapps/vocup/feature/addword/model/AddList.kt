package ru.uxapps.vocup.feature.addword.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.data.api.Language

internal interface AddList {

    val state: LiveData<State>
    val languages: LiveData<List<Language>>
    fun onRetry()
    fun onSave(item: DefItem)
    fun onChooseLang(lang: Language)

    sealed class State {
        data class Data(val items: List<DefItem>) : State()
        object Loading : State()
        object Error : State()
    }
}