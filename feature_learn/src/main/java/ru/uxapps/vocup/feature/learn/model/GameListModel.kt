package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.feature.learn.model.game.Game

internal interface GameListModel {
    val games: LiveData<List<GameItem>>
    val playEnabled: LiveData<Boolean>
}

internal data class GameItem(val game: Game, val enabled: Boolean)