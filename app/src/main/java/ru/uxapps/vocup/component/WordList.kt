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

interface WordList {

    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
    val onUndoRemoved: LiveEvent<() -> Unit>

    fun onRemove(word: Word)
}

class WordListImp(
        private val repo: Repo,
        private val scope: CoroutineScope
) : WordList {

    override val onUndoRemoved = MutableLiveEvent<() -> Unit>()
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
            onUndoRemoved.send {
                scope.launch {
                    repo.addWord(word)
                }
            }
        }
    }
}