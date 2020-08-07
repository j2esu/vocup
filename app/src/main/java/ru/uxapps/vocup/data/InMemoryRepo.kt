package ru.uxapps.vocup.data

import androidx.lifecycle.MutableLiveData

object InMemoryRepo : Repo {

    private val words = MutableLiveData<List<Word>>(listOf(
        Word("Hello"),
        Word("Word")
    ))

    override fun getAllWords() = words

}