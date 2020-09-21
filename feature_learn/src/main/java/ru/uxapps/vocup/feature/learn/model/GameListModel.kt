package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.LiveData

internal interface GameListModel {
    val games: LiveData<List<GameItem>>
    val playEnabled: LiveData<Boolean>
}

internal data class GameItem(
    val id: Int,
    val title: String,
    val desc: String,
    val error: String?
)