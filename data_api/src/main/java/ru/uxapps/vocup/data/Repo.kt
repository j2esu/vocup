package ru.uxapps.vocup.data

import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getAllWords(): Flow<List<Word>>
    fun getWord(text: String): Flow<Word?>
    fun getTargetLanguage(): Flow<Language>
    suspend fun getDefinitions(word: String, lang: Language): List<Def>
    suspend fun getCompletions(word: String): List<String>
    suspend fun addWord(text: String, trans: List<String>)
    suspend fun restoreWord(word: Word)
    suspend fun deleteWord(text: String)
    suspend fun setTargetLanguage(lang: Language)
    suspend fun updateTranslations(word: String, trans: List<String>)
    suspend fun addTranslations(word: String, trans: List<String>)
}