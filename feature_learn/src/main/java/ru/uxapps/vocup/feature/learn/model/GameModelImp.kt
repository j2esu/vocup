package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import ru.uxapps.vocup.feature.learn.model.game.GameAction
import ru.uxapps.vocup.feature.learn.model.game.WordToTranslationGame

internal class GameModelImp(
    gameId: Int,
    scope: CoroutineScope
) : GameModel {

    private val game = WordToTranslationGame(scope)

    override val state = game.state.asLiveData()

    override fun onAction(action: GameAction) {
        game.actions.offer(action as WordToTranslationGame.Action)
    }
}