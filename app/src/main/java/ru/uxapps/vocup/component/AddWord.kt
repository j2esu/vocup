package ru.uxapps.vocup.component

import androidx.core.text.trimmedLength
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.Language
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.util.LiveEvent
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

interface AddWordModel {
    val translation: LiveData<Translation.State?>
    val saveEnabled: LiveData<Boolean>
    val languages: LiveData<List<Language>>
    val onWordAdded: LiveEvent<String>
    fun onWordInput(text: String)
    fun onSave()
    fun chooseLang(lang: Language)
}

class AddWordImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : AddWordModel {

    private val transFeature = Translation(repo)
    private val wordInput = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)

    override val translation: LiveData<Translation.State?> =
        wordInput
            .map { it.trim() }
            .distinctUntilChanged()
            .debounce(400)
            .flatMapLatest {
                if (it.length > 1) transFeature.getTranslation(it) else flowOf(null)
            }
            .asLiveData()

    override val saveEnabled: LiveData<Boolean> =
        combine(wordInput, isLoading) { input, loading ->
            input.trimmedLength() > 1 && !loading
        }.asLiveData()

    override val languages: LiveData<List<Language>> =
        repo.getTargetLang().map { listOf(it) + Language.values() }.asLiveData()

    override val onWordAdded = MutableLiveEvent<String>()

    override fun onWordInput(text: String) {
        wordInput.value = text
    }

    override fun onSave() {
        isLoading.value = true
        val wordText = requireNotNull(wordInput.value)
        scope.launch {
            repo.addWord(wordText)
            onWordAdded.send(wordText)
        }
    }

    override fun chooseLang(lang: Language) {
        TODO("not implemented")
    }
}