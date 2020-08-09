package ru.uxapps.vocup.data

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

object InMemoryRepo : Repo {

    private val words = MutableLiveData<List<Word>>()

    init {
        GlobalScope.launch {
            delay(Random.nextLong(100, 2000))
            words.postValue(listOf(
                Word("Hello"),
                Word("Word"),
                Word("Test"),
                Word("Vocabulary"),
                Word("Apple"),
                Word("Pen"),
                Word("Cat"),
                Word("Dog")
            ))
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

    override suspend fun addWord(text: String) {
        words.value = words.value?.plus(Word(text))
    }
}