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
            delay(Random.nextLong(100, 2000))
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

    override suspend fun getTranslation(text: String): String {
        delay(Random.nextLong(100, 2000))
        if (Random.nextInt() % 4 == 0) {
            throw IOException("Can't load translation")
        }
        return "Translated to ${targetLang.value}: $text"
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

    override suspend fun addWord(text: String) {
        words.value = words.value?.plus(Word(text))
    }
}