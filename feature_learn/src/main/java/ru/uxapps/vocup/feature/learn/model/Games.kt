package ru.uxapps.vocup.feature.learn.model

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import ru.uxapps.vocup.feature.learn.R

internal interface Games {
    val games: LiveData<List<GameItem>>
    val playEnabled: LiveData<Boolean>
}

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

internal data class GameItem(val game: Game, val enabled: Boolean)