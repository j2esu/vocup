package ru.uxapps.vocup.feature.learn.model

import androidx.lifecycle.LiveData

internal interface GameListModel {
    val games: LiveData<List<GameItem>>
}

internal data class GameItem(
    val id: Int,
    val title: String,
    val desc: String,
    val error: String?
)