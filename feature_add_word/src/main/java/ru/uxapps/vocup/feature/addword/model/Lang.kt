package ru.uxapps.vocup.feature.addword.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.api.Repo

internal class Lang(
    private val scope: CoroutineScope,
    private val repo: Repo
) {

    val languages: LiveData<List<Language>> =
        repo.getTargetLanguage().map {
            listOf(it) + (Language.values().toList() - it)
        }.asLiveData(scope.coroutineContext + Dispatchers.IO)

    fun onChooseLang(lang: Language) {
        scope.launch {
            repo.setTargetLanguage(lang)
        }
    }
}
