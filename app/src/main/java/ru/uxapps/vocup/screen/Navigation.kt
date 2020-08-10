package ru.uxapps.vocup.screen

import ru.uxapps.vocup.data.Word

interface Navigation {
    fun openWord(word: Word)
    fun openAddWord()
}