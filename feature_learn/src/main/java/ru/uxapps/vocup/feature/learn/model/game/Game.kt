package ru.uxapps.vocup.feature.learn.model.game

import androidx.annotation.StringRes
import ru.uxapps.vocup.feature.learn.R

internal enum class Game(
    @StringRes val title: Int,
    @StringRes val desc: Int,
    @StringRes val requirement: Int
) {
    WordToTranslation(
        R.string.word_to_translation_title,
        R.string.word_to_translation_desc,
        R.string.word_to_translation_req
    )
}