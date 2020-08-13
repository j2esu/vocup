package ru.uxapps.vocup.data

import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getAllWords(): Flow<List<Word>>
    suspend fun getDetails(word: String): String
    suspend fun addWord(text: String)
    suspend fun removeWord(text: String)
    fun getTargetLang(): Flow<Language>
    suspend fun setTargetLang(lang: Language)
    suspend fun getTranslation(word: String, lang: Language): List<Trans>
}

data class Word(val text: String)

data class Trans(
    val text: String,
    val meanings: List<String>
)

enum class Language(val code: String) {
    English("en"),
    Czech("cs"),
    Danish("da"),
    German("de"),
    Greek("el"),
    Spanish("es"),
    Estonian("et"),
    Finnish("fi"),
    French("fr"),
    Italian("it"),
    Lithuanian("lt"),
    Latvian("lv"),
    Dutch("nl"),
    Norwegian("no"),
    Portuguese("pt"),
    Russian("ru"),
    Slovak("sk"),
    Swedish("sv"),
    Turkish("tr"),
    Ukrainian("uk")
}