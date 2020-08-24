package ru.uxapps.vocup.core.data

interface Api {
    suspend fun getDefinitions(words: List<String>, lang: Language): List<Def>
    suspend fun getPredictions(input: String): List<String>
    suspend fun getPronunciations(word: String): List<String>
    suspend fun getCompletions(input: String): List<String>
}