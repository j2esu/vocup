package ru.uxapps.vocup.feature.dictionary.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send

internal class DictionaryImp(
    private val repo: Repo,
    private val scope: CoroutineScope
) : Dictionary {

    override val onWordRemoved = MutableLiveEvent<suspend () -> Unit>()
    override val words: LiveData<List<Word>> = repo.getAllWords().asLiveData(Dispatchers.IO)
    override val loading: LiveData<Boolean> = liveData {
        emit(true)
        emitSource(words.map { false })
    }

    override fun onRemove(word: Word) {
        scope.launch {
            repo.deleteWord(word.id)
            onWordRemoved.send { repo.restoreWord(word) }
        }
    }
}
