package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.model.game.Game

internal class GameListModelImp(repo: Repo, scope: CoroutineScope) : GameListModel {

    private val requirements = mapOf<Game, (List<Word>) -> Boolean>(
        Game.WordToTranslation to { words ->
            words.size > 2
        }
    )

    override val games = repo.getAllWords().map { words ->
        requirements.map { (ex, req) ->
            GameItem(ex, req(words))
        }
    }.asLiveData(scope.coroutineContext + Dispatchers.IO)

    override val playEnabled = liveData {
        emit(false)
        emitSource(games.map { games ->
            games.any { it.enabled }
        })
    }
}