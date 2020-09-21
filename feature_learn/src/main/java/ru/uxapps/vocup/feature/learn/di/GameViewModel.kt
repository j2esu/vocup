package ru.uxapps.vocup.feature.learn.di

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.uxapps.vocup.data.RepoProvider

internal class GameViewModel(private val app: Application) : AndroidViewModel(app) {

    private var gameComponent: GameComponent? = null

    fun getGameComponent(gameId: Int): GameComponent {
        return gameComponent ?: DaggerGameComponent.factory().create(this, gameId, app as RepoProvider)
            .also { gameComponent = it }
    }
}