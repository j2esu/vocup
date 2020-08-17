package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.util.asStateFlow

interface WordDetails {
    val text: LiveData<String>
    val translations: LiveData<List<String>?>
    fun onReorderTrans(newTrans: List<String>)
    fun onAddTrans(text: String)
    fun onEditTrans(trans: String, newText: String)
}

class WordDetailsImp(
    private val wordText: String,
    private val repo: Repo,
    private val scope: CoroutineScope
) : WordDetails {

    private val word = repo.getWord(wordText).filterNotNull().asStateFlow(scope)

    override val text = word.mapNotNull { it?.text }.onStart { emit(wordText) }.asLiveData()
    override val translations = word.map { it?.translations }.onStart { emit(null) }.asLiveData()

    override fun onReorderTrans(newTrans: List<String>) {
        scope.launch {
            repo.setTranslations(wordText, newTrans)
        }
    }

    override fun onAddTrans(text: String) {
        scope.launch {
            translations.value?.let {
                repo.setTranslations(wordText, listOf(text) + it)
            }
        }
    }

    override fun onEditTrans(trans: String, newText: String) {
        scope.launch {
            translations.value?.let { currentTrans ->
                val newTrans = currentTrans.toMutableList().apply {
                    val index = indexOfFirst { it == trans }
                    if (newText.isNotBlank()) {
                        set(index, newText)
                    } else {
                        removeAt(index)
                    }
                }
                repo.setTranslations(wordText, newTrans)
            }
        }
    }
}