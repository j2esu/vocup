package ru.uxapps.vocup.feature.learn.model.game

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow

internal interface GameContract<S : GameState, E : GameEvent, A : GameAction> {
    val state: Flow<S>
    val events: ReceiveChannel<E>
    val actions: SendChannel<A>
}

interface GameState
interface GameEvent
interface GameAction