package ru.uxapps.vocup.screen

import ru.uxapps.vocup.util.LiveEvent

interface Navigation {
    val onDeleteWord: LiveEvent<String>
    fun openWord(word: String)
    fun openAddWord()
    fun up()
}