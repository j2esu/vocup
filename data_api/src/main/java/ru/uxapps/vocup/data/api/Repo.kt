package ru.uxapps.vocup.data.api

import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getAllWords(): Flow<List<Word>>
    fun getWord(wordId: Long): Flow<Word?>
    fun getTargetLanguage(): Flow<Language>
    suspend fun getDefinitions(word: String): List<Def>
    suspend fun getDefinitions(words: List<String>): List<Def>
    suspend fun getCompletions(word: String): List<String>
    suspend fun addWord(text: String, trans: List<String>)
    suspend fun restoreWord(word: Word)
    suspend fun deleteWord(wordId: Long)
    suspend fun setTargetLanguage(lang: Language)
    suspend fun setTranslations(wordId: Long, trans: List<String>)
    suspend fun addTranslations(wordId: Long, trans: List<String>)
    suspend fun updateProgress(word: Word, progressDiff: Int)
    suspend fun getWordKits(): Flow<List<Kit>>
    suspend fun dismissKit(kit: Kit)
    suspend fun restoreKit(kit: Kit)
}