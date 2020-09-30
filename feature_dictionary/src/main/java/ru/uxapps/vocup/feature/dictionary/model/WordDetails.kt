package ru.uxapps.vocup.feature.dictionary.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.util.LiveEvent

internal interface WordDetails {
    val text: LiveData<String>
    val pron: LiveData<String>
    val translations: LiveData<List<String>?>
    val onTransDeleted: LiveEvent<suspend () -> Unit>
    val onWordDeleted: LiveEvent<suspend () -> Unit>
    fun onReorderTrans(newTrans: List<String>)
    fun onAddTrans(text: String)
    fun onEditTrans(trans: String, newText: String)
    fun onDeleteTrans(trans: String)
    fun onDeleteWord()
}