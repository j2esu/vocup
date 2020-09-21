package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.feature.learn.model.game.GameAction
import ru.uxapps.vocup.feature.learn.model.game.GameState

internal interface GameModel {
    val state: LiveData<out GameState>
    fun onAction(action: GameAction)
}