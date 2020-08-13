package ru.uxapps.vocup.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> Flow<T>.asStateFlow(scope: CoroutineScope): StateFlow<T?> {
    val stateFlow = MutableStateFlow<T?>(null)
    scope.launch {
        collect {
            stateFlow.value = it
        }
    }
    return stateFlow
}