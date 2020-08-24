package ru.uxapps.vocup.component

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.uxapps.vocup.core.data.Repo
import ru.uxapps.vocup.core.data.Word
import ru.uxapps.vocup.util.LiveEvent
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

interface Dictionary {

    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
    val onWordRemoved: LiveEvent<Word>

    fun onRemove(word: Word)
    fun onRemove(wordText: String)
    fun restoreWord(word: Word)
}

class DictionaryImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : Dictionary {

    override val onWordRemoved = MutableLiveEvent<Word>()
    override val words: LiveData<List<Word>> = repo.getAllWords().asLiveData()
    override val loading: LiveData<Boolean> =
        repo.getAllWords()
            .map { it as List<Word>? }
            .onStart { emit(null) }
            .map { it == null }
            .asLiveData()

    override fun onRemove(word: Word) {
        scope.launch {
            repo.deleteWord(word.text)
            onWordRemoved.send(word)
        }
    }

    override fun onRemove(wordText: String) {
        words.value?.find { it.text == wordText }?.let { onRemove(it) }
    }

    override fun restoreWord(word: Word) {
        scope.launch {
            repo.restoreWord(word)
        }
    }
}