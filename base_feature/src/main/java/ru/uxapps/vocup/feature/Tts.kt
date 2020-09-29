package ru.uxapps.vocup.feature

import androidx.fragment.app.Fragment

interface Tts {
    fun speak(text: String)
}

interface TtsProvider {
    val tts: Tts
}

val Fragment.tts: Tts get() = (activity as TtsProvider).tts