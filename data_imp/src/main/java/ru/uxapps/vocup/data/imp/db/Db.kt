package ru.uxapps.vocup.data.imp.db

import kotlinx.coroutines.flow.Flow
import ru.uxapps.vocup.data.api.Word

interface Db {
    fun getAllWords(): Flow<List<Word>>
    fun getWord(wordId: Long): Flow<Word?>
    suspend fun addWord(text: String, trans: List<String>, pron: String?)
    suspend fun restoreWord(word: Word)
    suspend fun deleteWord(wordId: Long)
    suspend fun updateTranslations(wordId: Long, trans: List<String>)
    suspend fun updateProgress(wordId: Long, progress: Int)
}