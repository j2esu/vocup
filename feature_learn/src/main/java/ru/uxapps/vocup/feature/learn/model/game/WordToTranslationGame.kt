package ru.uxapps.vocup.feature.learn.model.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.uxapps.vocup.feature.learn.model.game.WordToTranslationGame.Action
import ru.uxapps.vocup.feature.learn.model.game.WordToTranslationGame.State

internal class WordToTranslationGame(
    scope: CoroutineScope
) : GameContract<State, Nothing, Action> {

    override val state = MutableStateFlow<State>(State.Init)
    override val events = Channel<Nothing>()
    override val actions = Channel<Action>(Channel.UNLIMITED)

    init {
        state.value = State.Data("Hello", listOf("Привет", "Медвед"), emptySet())
        scope.launch {
            for (action in actions) {
                state.value = when (val oldState = state.value) {
                    is State.Data -> when (action) {
                        is Action.OnClick -> oldState.copy(
                            checked = (oldState.checked + oldState.translations[action.pos])
                        )
                    }
                    else -> error("Unexpected state $oldState")
                }
            }
        }
    }

    sealed class State : GameState {
        object Init : State()
        data class Data(
            val word: String,
            val translations: List<String>,
            val checked: Set<String>
        ) : State()
    }

    sealed class Action : GameAction {
        data class OnClick(val pos: Int) : Action()
    }
}