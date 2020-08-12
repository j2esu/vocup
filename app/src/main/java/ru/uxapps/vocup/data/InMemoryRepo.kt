package ru.uxapps.vocup.data

import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

object InMemoryRepo : Repo {

    private val words: MutableStateFlow<List<Word>?> = MutableStateFlow(null)
    private val targetLang = MutableStateFlow(suggestTargetLang())

    init {
        GlobalScope.launch {
            delay(1000)
            words.value = listOf(
                Word("Hello"),
                Word("Word"),
                Word("Test"),
                Word("Vocabulary"),
                Word("Apple"),
                Word("Pen"),
                Word("Cat"),
                Word("Dog")
            )
        }
        GlobalScope.launch {
            getTargetLangPref()?.let {
                targetLang.value = it
            }
        }
    }

    override fun getAllWords() = words.filterNotNull()

    override suspend fun getDetails(word: String): String {
        delay(1000)
        return "Details for: $word"
    }

    override fun getTargetLang(): Flow<Language> = targetLang

    private suspend fun getTargetLangPref(): Language? {
        delay(3000)
        return null
    }

    private fun suggestTargetLang(): Language {
        val userLocales = LocaleListCompat.getAdjustedDefault()
        for (i in 0 until userLocales.size()) {
            val supportedLang = Language.values().find { it.code == userLocales[i].language }
            if (supportedLang != null) {
                return supportedLang
            }
        }
        return Language.English
    }

    override suspend fun setTargetLang(lang: Language) {
        targetLang.value = lang
    }

    override suspend fun getTranslation(word: String, lang: Language): List<Definition> {
        delay(1000)
        if (Random.nextInt() % 3 == 0) {
            throw IOException("Can't load translation")
        } else if (Random.nextInt() % 3 == 0) {
            return emptyList()
        }
        return listOf(
            Definition(
                "hello",
                "существительное",
                "həˈləʊ",
                listOf(
                    Translation(
                        "привет",
                        listOf("добрый день", "Здравствуй"),
                        listOf("hi", "good afternoon"),
                        listOf(
                            Example("big hello", "большой привет")
                        )
                    ),
                    Translation("приветствие", emptyList(), emptyList(), emptyList())
                )
            ),
            Definition(
                "hello",
                "глагол",
                "həˈləʊ",
                listOf(
                    Translation("здравствуйте", emptyList(), listOf("hi"), emptyList()),
                    Translation("поздороваться", emptyList(), listOf("greet"), emptyList()),
                    Translation("приветствовать", emptyList(), emptyList(), emptyList())
                )
            ),
            Definition(
                "hello",
                "междометие",
                "həˈləʊ",
                listOf(
                    Translation("АЛЛО", emptyList(), emptyList(), emptyList()),
                    Translation("ау", emptyList(), emptyList(), emptyList())
                )
            )
        )
    }

    override suspend fun addWord(text: String) {
        words.value = words.value?.plus(Word(text))
    }
}