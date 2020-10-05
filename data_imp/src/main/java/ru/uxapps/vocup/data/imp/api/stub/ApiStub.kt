package ru.uxapps.vocup.data.imp.api.stub

import ru.uxapps.vocup.data.api.Def
import ru.uxapps.vocup.data.api.Kit
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.imp.api.Api
import kotlin.random.Random

class ApiStub : Api {

    override suspend fun getDefinitions(words: List<String>, userLang: Language): List<Def> {
        return words.map { word ->
            Def(word, List(Random.nextInt(5)) { "Перевод $word #$it" })
        }
    }

    override suspend fun getWordKits(userLang: Language): List<Kit> = listOf(
        Kit(1, "Top #1", listOf("Hello", "World")),
        Kit(2, "Top #1", listOf("How", "Are", "You"))
    )

    override suspend fun getPredictions(input: String, userLang: Language): List<String> = emptyList()
    override suspend fun getPronunciations(word: String): List<String> = emptyList()
    override suspend fun getCompletions(input: String): List<String> = emptyList()
}