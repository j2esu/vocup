package ru.uxapps.vocup.data.api

import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getAllWords(): Flow<List<Word>>
    fun getWord(wordId: Long): Flow<Word?>
    fun getTargetLanguage(): Flow<Language>
    suspend fun getDefinitions(word: String, lang: Language): List<Def>
    suspend fun getCompletions(word: String): List<String>
    suspend fun addWord(text: String, trans: List<String>)
    suspend fun restoreWord(word: Word)
    suspend fun deleteWord(wordId: Long)
    suspend fun setTargetLanguage(lang: Language)
    suspend fun updateTranslations(wordId: Long, trans: List<String>)
    suspend fun addTranslations(wordId: Long, trans: List<String>)
}