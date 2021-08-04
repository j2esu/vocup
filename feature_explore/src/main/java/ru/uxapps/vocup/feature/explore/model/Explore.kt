package ru.uxapps.vocup.feature.explore.model

import androidx.lifecycle.LiveData
import ru.uxapps.vocup.data.api.Kit
import ru.uxapps.vocup.util.LiveEvent

internal interface Explore {

    val kits: LiveData<State>
    val onKitDismissed: LiveEvent<suspend () -> Unit>
    fun onRetry()
    fun onDismiss(kitItem: KitItem)

    sealed class State {
        data class Data(val kits: List<KitItem>) : State()
        object Loading : State()
        object Error : State()
    }
}

internal data class KitItem(
    val kit: Kit,
    val newWords: List<String>
)
