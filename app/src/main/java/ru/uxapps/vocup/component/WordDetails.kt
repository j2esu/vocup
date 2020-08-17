package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.util.LiveEvent
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.asStateFlow
import ru.uxapps.vocup.util.send

interface WordDetails {
    val text: LiveData<String>
    val translations: LiveData<List<String>?>
    val onWordNotFound: LiveEvent<Unit>
    fun onReorderTrans(newTrans: List<String>)
}

class WordDetailsImp(
    private val wordText: String,
    private val repo: Repo,
    private val scope: CoroutineScope
) : WordDetails {

    override val onWordNotFound = MutableLiveEvent<Unit>()

    private val word = flow {
        val word = repo.getWord(wordText)
        if (word == null) {
            onWordNotFound.send()
        }
        emit(word)
    }.asStateFlow(scope)

    override val text = word.mapNotNull { it?.text }.onStart { emit(wordText) }.asLiveData()
    override val translations = word.map { it?.translations }.onStart { emit(null) }.asLiveData()

    override fun onReorderTrans(newTrans: List<String>) {
        scope.launch {
            repo.setTranslations(wordText, newTrans)
        }
    }
}