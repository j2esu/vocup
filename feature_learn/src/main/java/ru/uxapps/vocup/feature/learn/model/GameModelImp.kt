package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.GameContract
import ru.uxapps.vocup.feature.learn.game.WordToTranslationModel

internal class GameModelImp(
    gameId: Int,
    scope: CoroutineScope,
    repo: Repo
) : GameModel {

    private val game = WordToTranslationModel(scope, repo)

    override val state = game.state.asLiveData()

    override fun onAction(action: GameContract.Action) {
        game.proceed(action)
    }
}