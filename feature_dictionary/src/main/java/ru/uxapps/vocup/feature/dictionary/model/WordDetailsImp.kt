package ru.uxapps.vocup.feature.dictionary.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send
import ru.uxapps.vocup.util.toStateFlow

internal class WordDetailsImp(
    private val wordId: Long,
    private val repo: Repo,
    private val scope: CoroutineScope
) : WordDetails {

    private val word = repo.getWord(wordId).filterNotNull().toStateFlow(scope)

    override val text = word.mapNotNull { it?.text }.asLiveData(Dispatchers.IO)
    override val pron = word.map { it?.pron ?: "" }.asLiveData(Dispatchers.IO)
    override val translations = word.map { it?.translations }.onStart { emit(null) }
        .asLiveData(Dispatchers.IO)
    override val onTransDeleted = MutableLiveEvent<suspend () -> Unit>()
    override val onWordDeleted = MutableLiveEvent<suspend () -> Unit>()

    override fun onReorderTrans(newTrans: List<String>) {
        scope.launch {
            repo.setTranslations(wordId, newTrans)
        }
    }

    override fun onAddTrans(text: String) {
        scope.launch {
            translations.value?.let {
                repo.setTranslations(wordId, it + text)
            }
        }
    }

    override fun onDeleteTrans(trans: String) {
        val currentTrans = translations.value!!
        val newTrans = currentTrans.toMutableList().apply { remove(trans) }
        scope.launch {
            repo.setTranslations(wordId, newTrans)
            onTransDeleted.send { repo.setTranslations(wordId, currentTrans) }
        }
    }

    override fun onDeleteWord() {
        word.value?.let { word ->
            scope.launch {
                repo.deleteWord(word.id)
                onWordDeleted.send { repo.restoreWord(word) }
            }
        }
    }

    override fun onEditTrans(trans: String, newText: String) {
        val currentTrans = translations.value!!
        val newTrans = currentTrans.toMutableList().apply {
            set(indexOfFirst { it == trans }, newText)
        }
        scope.launch {
            repo.setTranslations(wordId, newTrans)
        }
    }
}
