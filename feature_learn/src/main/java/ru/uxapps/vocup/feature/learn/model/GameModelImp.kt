package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.Game
import ru.uxapps.vocup.feature.learn.game.GameContract
import ru.uxapps.vocup.feature.learn.game.TransToWordModel
import ru.uxapps.vocup.feature.learn.game.WordToTransModel

internal class GameModelImp(gameId: Int, scope: CoroutineScope, repo: Repo) : GameModel {

    private val game = requireNotNull(MODEL_PROVIDERS[Game.values()[gameId]]).provide(scope, repo)

    override val state = game.state.asLiveData()
    override fun onAction(action: GameContract.Action) = game.proceed(action)
}

private interface ModelProvider {
    fun provide(scope: CoroutineScope, repo: Repo): GameContract.Model
}

private val MODEL_PROVIDERS = mapOf(
    Game.WordToTranslation to object : ModelProvider {
        override fun provide(scope: CoroutineScope, repo: Repo) = WordToTransModel(scope, repo)
    },
    Game.TranslationToWord to object : ModelProvider {
        override fun provide(scope: CoroutineScope, repo: Repo) = TransToWordModel(scope, repo)
    }
)