package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.Repo
import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.util.LiveEvent
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

interface Dictionary {

    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
    val onUndoRemoved: LiveEvent<Runnable>

    fun onRemove(word: Word)
    fun onRemove(wordText: String)
}

class DictionaryImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : Dictionary {

    override val onUndoRemoved = MutableLiveEvent<Runnable>()
    override val words: LiveData<List<Word>> = repo.getAllWords().asLiveData()
    override val loading: LiveData<Boolean> =
        repo.getAllWords()
            .map { it as List<Word>? }
            .onStart { emit(null) }
            .map { it == null }
            .asLiveData()

    override fun onRemove(word: Word) {
        scope.launch {
            repo.removeWord(word)
            onUndoRemoved.send(Runnable {
                scope.launch {
                    repo.addWord(word)
                }
            })
        }
    }

    override fun onRemove(wordText: String) {
        words.value?.find { it.text == wordText }?.let { onRemove(it) }
    }
}