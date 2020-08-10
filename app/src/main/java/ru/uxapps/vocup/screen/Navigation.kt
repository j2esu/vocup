package ru.uxapps.vocup.screen

import androidx.fragment.app.Fragment
import ru.uxapps.vocup.data.Word

interface Navigation {
    fun openWord(word: Word)
    fun openAddWord()
}

val Fragment.nav get() = activity as Navigation