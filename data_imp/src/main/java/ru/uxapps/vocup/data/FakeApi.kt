package ru.uxapps.vocup.data

import kotlinx.coroutines.delay
import java.io.IOException
import kotlin.random.Random

object FakeApi : Api {

    override suspend fun getDefinitions(words: List<String>, lang: Language): List<Def> {
        delay(1000)
        if (Random.nextInt() % 4 == 0) {
            if (Random.nextBoolean()) {
                throw IOException("Can't load translation")
            } else {
                return emptyList()
            }
        }
        return words.map { word ->
            Def("$word (${lang.code})",
                List(Random.nextInt(1, 10)) { transIndex ->
                    "Translation $transIndex"
                }
            )
        }
    }

    override suspend fun getPredictions(input: String): List<String> {
        delay(100)
        return List(Random.nextInt(10)) { "$input (p$it)" }
    }

    override suspend fun getPronunciations(word: String): List<String> {
        delay(100)
        return if (Random.nextBoolean()) listOf(word) else emptyList()
    }

    override suspend fun getCompletions(input: String): List<String> {
        delay(100)
        return List(Random.nextInt(10)) { "$input (c$it)" }
    }
}