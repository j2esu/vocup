package ru.uxapps.vocup.data

import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

object InMemoryRepo : Repo {

    private val words: MutableStateFlow<List<Word>?> = MutableStateFlow(null)

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
    }

    override fun getAllWords() = words

    override suspend fun getTranslation(text: String): String {
        delay(Random.nextLong(100, 2000))
        if (Random.nextInt() % 4 == 0) {
            throw IOException("Can't load translation")
        }
        return "Translated: $text"
    }

    override fun getTargetLang(): Flow<Language> = flow {
        emit(getTargetLangPref() ?: suggestTargetLang())
    }

    private suspend fun getTargetLangPref(): Language? = null // TODO: 8/10/2020 get from prefs

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

    override suspend fun setTargetLang(long: Language) {
        TODO("not implemented")
    }

    override suspend fun addWord(text: String) {
        words.value = words.value?.plus(Word(text))
    }
}