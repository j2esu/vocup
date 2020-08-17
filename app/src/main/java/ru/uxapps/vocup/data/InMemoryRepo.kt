package ru.uxapps.vocup.data

import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
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
                Word("World", listOf("Мир", "Вселенная", "Общество", "Свет"), System.nanoTime()),
                Word("Hello", listOf("Привет", "Здравствуй", "Алло"), System.nanoTime() + 1)
            )
        }
        GlobalScope.launch {
            getTargetLangPref()?.let {
                targetLang.value = it
            }
        }
    }

    override fun getAllWords() = words.filterNotNull().map { it.sortedBy(Word::created).reversed() }

    override fun getWord(text: String): Flow<Word?> =
        getAllWords()
            .map { words ->
                words.find { it.text == text }
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

    override suspend fun getTranslations(word: String, lang: Language): List<Def> {
        delay(1000)
        if (Random.nextInt(5) == 0) {
            if (Random.nextBoolean()) {
                throw IOException("Can't load translation")
            } else {
                return emptyList()
            }
        }
        return List(Random.nextInt(1, 5)) { defIndex ->
            Def("$word $defIndex (${lang.code})",
                List(Random.nextInt(1, 30)) { transIndex ->
                    "Translation $transIndex"
                }
            )
        }
    }

    override suspend fun addWord(def: Def) =
        addWordInner(def.text, def.translations, System.nanoTime())

    override suspend fun addWord(word: Word) =
        addWordInner(word.text, word.translations, word.created)

    private fun addWordInner(text: String, translations: List<String>, created: Long) {
        words.value = words.value?.let { listOf(Word(text, translations, created)) + it }
    }

    override suspend fun removeWord(def: Def) = removeWordInner(def.text)
    override suspend fun removeWord(word: Word) = removeWordInner(word.text)

    private fun removeWordInner(text: String) {
        words.value = words.value?.filter { it.text != text }
    }

    override suspend fun setTranslations(word: String, trans: List<String>) {
        withWord(word) { currentWords, wordIndex ->
            words.value = currentWords.toMutableList().apply {
                set(wordIndex, get(wordIndex).copy(translations = trans))
            }
        }
    }

    override suspend fun addTranslation(word: String, trans: String) {
        withWord(word) { currentWords, wordIndex ->
            words.value = currentWords.toMutableList().apply {
                val newTrans = listOf(trans) + get(wordIndex).translations
                set(wordIndex, get(wordIndex).copy(translations = newTrans))
            }
        }
    }

    private fun withWord(word: String, action: (List<Word>, Int) -> Unit) {
        val currentWords = words.value
        if (currentWords != null) {
            val wordIndex = currentWords.indexOfFirst { it.text == word }
            if (wordIndex != -1) {
                action(currentWords, wordIndex)
            }
        }
    }
}