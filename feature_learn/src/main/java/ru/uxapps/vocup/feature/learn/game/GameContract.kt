package ru.uxapps.vocup.feature.learn.game

import kotlinx.coroutines.flow.Flow

internal interface GameContract {

    interface Model {
        val state: Flow<State?>
        fun proceed(action: Action)
    }

    interface View {
        fun render(state: State)
        var onAction: ((Action) -> Unit)?
    }

    interface State
    interface Action
}