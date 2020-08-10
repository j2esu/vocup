package ru.uxapps.vocup.data

import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getAllWords(): Flow<List<Word>?>
    suspend fun getTranslation(text: String): String
    suspend fun addWord(text: String)
}

data class Word(val text: String)