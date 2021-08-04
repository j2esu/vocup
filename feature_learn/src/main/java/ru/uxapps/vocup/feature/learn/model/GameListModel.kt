package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.feature.learn.game.Game

internal interface GameListModel {
    val games: LiveData<List<GameItem>>
}

internal data class GameItem(
    val game: Game,
    val error: String?
)
