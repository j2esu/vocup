package ru.uxapps.vocup.screen

import ru.uxapps.vocup.data.Word
import ru.uxapps.vocup.util.LiveEvent

interface Navigation {
    val onDeleteWord: LiveEvent<Word>
    fun openWord(word: Word)
    fun openAddWord()
    fun up()
}