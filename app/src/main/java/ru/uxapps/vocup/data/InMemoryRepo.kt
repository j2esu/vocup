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
            words.value = listOf(Word(Trans("Hello", listOf("Привет", "Здравствуй"))))
        }
        GlobalScope.launch {
            getTargetLangPref()?.let {
                targetLang.value = it
            }
        }
    }

    override fun getAllWords() = words.filterNotNull()

    override suspend fun getWord(text: String): Word? {
        delay(1000)
        return words.value?.find { it.trans.text == text }
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
        return Language.Russian
    }

    override suspend fun setTargetLang(lang: Language) {
        targetLang.value = lang
    }

    override suspend fun getTranslations(word: String, lang: Language): List<Trans> {
        delay(1000)
        if (Random.nextInt(5) == 0) {
            if (Random.nextBoolean()) {
                throw IOException("Can't load translation")
            } else {
                return emptyList()
            }
        }
        return List(Random.nextInt(1, 5)) { transIndex ->
            Trans("$word $transIndex (${lang.code})",
                List(Random.nextInt(1, 10)) { meanIndex ->
                    "Meaning $meanIndex"
                }
            )
        }
    }

    override suspend fun addWord(trans: Trans) {
        words.value = listOf(Word(trans)) + (words.value ?: emptyList())
    }

    override suspend fun removeWord(trans: Trans) {
        words.value = words.value?.filter { it.trans.text != trans.text }
    }
}