package ru.uxapps.vocup.data.imp.db

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Word

object FakeDb : Db {

    private val words: MutableStateFlow<List<Word>?> = MutableStateFlow(null)

    init {
        GlobalScope.launch {
            delay(1000)
            words.value = listOf(
                Word("Vocup!", listOf("Приложение", "Для запоминания", "Новых", "Слов"), null),
                Word("World", listOf("Мир", "Вселенная", "Общество", "Свет"), "wərld"),
                Word("Hello", listOf("Привет", "Здравствуй", "Алло"), "həˈlō")
            )
        }
    }

    override fun getAllWords(): Flow<List<Word>> =
        words.filterNotNull().map { it.sortedBy(Word::created).reversed() }

    override fun getWord(text: String): Flow<Word?> =
        getAllWords().map { words ->
            words.find { it.text == text }
        }

    override suspend fun restoreWord(word: Word) = addWordInner(word)

    private fun addWordInner(word: Word) {
        words.value = words.value?.let { listOf(word) + it }
    }

    override suspend fun addWord(text: String, trans: List<String>, pron: String?) =
        addWordInner(Word(text, trans, pron))

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