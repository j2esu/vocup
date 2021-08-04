package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.feature.learn.game.GameContract

internal interface GameModel {
    val state: LiveData<out GameContract.State?>
    fun onAction(action: GameContract.Action)
}
