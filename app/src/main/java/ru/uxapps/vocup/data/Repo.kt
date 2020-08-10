package ru.uxapps.vocup.data

import androidx.lifecycle.LiveData

interface Repo {
    fun getAllWords(): LiveData<List<Word>>
    suspend fun getTranslation(text: String): String
    suspend fun addWord(text: String)
}

data class Word(val text: String)