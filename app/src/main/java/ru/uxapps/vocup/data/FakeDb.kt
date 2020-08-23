package ru.uxapps.vocup.data

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

object FakeDb : Db {

    private val words: MutableStateFlow<List<Word>?> = MutableStateFlow(null)

    init {
        GlobalScope.launch {
            delay(1000)
            words.value = listOf(
                Word(
                    "Vocup!", listOf("Приложение", "Для запоминания", "Новых", "Слов"),
                    System.nanoTime(), null
                ),
                Word(
                    "World", listOf("Мир", "Вселенная", "Общество", "Свет"),
                    System.nanoTime() + 1, "wərld"
                ),
                Word(
                    "Hello", listOf("Привет", "Здравствуй", "Алло"),
                    System.nanoTime() + 2, "həˈlō"
                )
            )
        }
    }

    override fun getAllWords(): Flow<List<Word>> =
        words.filterNotNull().map { it.sortedBy(Word::created).reversed() }

    override fun getWord(text: String): Flow<Word?> =
        getAllWords().map { words ->
            words.find { it.text == text }
        }

    override suspend fun restoreWord(word: Word) =
        addWordInner(word.text, word.translations, word.created, word.pron)

    private fun addWordInner(text: String, translations: List<String>, created: Long, pron: String?) {
        words.value = words.value?.let { listOf(Word(text, translations, created, pron)) + it }
    }

    override suspend fun addWord(text: String, trans: List<String>, pron: String?) =
        addWordInner(text, trans, System.nanoTime(), pron)

    override suspend fun deleteWord(text: String) {
        words.value = words.value?.filter { it.text != text }
    }

    override suspend fun updateTranslations(word: String, trans: List<String>) {
        val currentWords = words.value
        if (currentWords != null) {
            val wordIndex = currentWords.indexOfFirst { it.text == word }
            if (wordIndex != -1) {
                words.value = currentWords.toMutableList().apply {
                    set(wordIndex, get(wordIndex).copy(translations = trans))
                }
            }
        }
    }
}