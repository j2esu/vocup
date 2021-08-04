package ru.uxapps.vocup.feature.addword.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.data.api.Language

internal interface AddWord {

    val state: LiveData<State>
    val maxWordLength: Int
    val languages: LiveData<List<Language>>
    fun onInput(text: String)
    fun onSave(item: DefItem)
    fun onChooseLang(lang: Language)
    fun onRetry()
    fun onSearch(text: String)

    sealed class State {
        object Idle : State()
        object Loading : State()
        data class Definitions(val items: List<DefItem>, val error: Boolean) : State()
        data class Completions(val items: List<String>) : State()
    }

}
