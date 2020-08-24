package ru.uxapps.vocup.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

fun <T> Flow<T>.toStateFlow(scope: CoroutineScope): StateFlow<T?> {
    val stateFlow = MutableStateFlow<T?>(null)
    scope.launch {
        collect {
            stateFlow.value = it
        }
    }
    return stateFlow
}

fun <T, S> Flow<T>.combine(other: Flow<S>): Flow<Pair<T, S>> = combine(other) { a, b -> a to b }
fun <T> Flow<T>.repeatWhen(other: Flow<*>): Flow<T> =
    combine(other.onStart { emit(null) }) { a, _ -> a }