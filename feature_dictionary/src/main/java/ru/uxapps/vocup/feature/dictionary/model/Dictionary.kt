package ru.uxapps.vocup.feature.dictionary.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.util.LiveEvent

internal interface Dictionary {

    val words: LiveData<List<Word>>
    val loading: LiveData<Boolean>
    val onWordRemoved: LiveEvent<suspend () -> Unit>

    fun onRemove(word: Word)
}

