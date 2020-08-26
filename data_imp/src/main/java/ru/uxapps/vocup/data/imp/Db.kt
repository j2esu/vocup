package ru.uxapps.vocup.data.imp

import kotlinx.coroutines.flow.Flow
import ru.uxapps.vocup.data.api.Word

interface Db {
    fun getAllWords(): Flow<List<Word>>
    fun getWord(text: String): Flow<Word?>
    suspend fun addWord(text: String, trans: List<String>, pron: String?)
    suspend fun restoreWord(word: Word)
    suspend fun deleteWord(text: String)
    suspend fun updateTranslations(word: String, trans: List<String>)
}