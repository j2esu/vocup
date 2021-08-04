package ru.uxapps.vocup.feature.learn.model

import android.content.Context
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.learn.game.Game

internal class GameListModelImp(context: Context, repo: Repo, scope: CoroutineScope) : GameListModel {

    override val games = repo.getAllWords().map { words ->
        Game.values().map { game ->
            GameItem(game, game.getRequirements(words)?.let { context.getString(it.first, *it.second) })
        }
    }.asLiveData(scope.coroutineContext + Dispatchers.IO)
}
