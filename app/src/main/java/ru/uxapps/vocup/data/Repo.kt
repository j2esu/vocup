package ru.uxapps.vocup.data

import androidx.lifecycle.LiveData

interface Repo {
    fun getAllWords(): LiveData<List<Word>>
}

object RepoProvider {
    fun provideRepo(): Repo = InMemoryRepo
}

data class Word(val text: String)