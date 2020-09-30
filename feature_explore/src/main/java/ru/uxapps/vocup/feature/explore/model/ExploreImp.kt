package ru.uxapps.vocup.feature.explore.model

import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.feature.explore.model.Explore.State.*
import ru.uxapps.vocup.util.MutableLiveEvent
import ru.uxapps.vocup.util.send
import java.io.IOException

internal class ExploreImp(
    private val scope: CoroutineScope,
    private val repo: Repo
) : Explore {

    private val retry = Channel<Unit>()

    override val kits = retry.receiveAsFlow().onStart { emit(Unit) }.transformLatest {
        emit(Loading)
        try {
            val currentWords = repo.getAllWords().map { words -> words.map { it.text }.toSet() }
            emitAll(currentWords.combine(repo.getWordKits()) { words, kits ->
                Data(kits.map { kit ->
                    KitItem(kit, kit.words.filterNot { words.contains(it.text) })
                })
            })
        } catch (e: IOException) {
            emit(Error)
        }
    }.asLiveData(scope.coroutineContext + Dispatchers.IO)

    override val onKitDismissed = MutableLiveEvent<suspend () -> Unit>()

    override fun onRetry() {
        retry.offer(Unit)
    }

    override fun onDismiss(kitItem: KitItem) {
        scope.launch(Dispatchers.IO) {
            repo.dismissKit(kitItem.kit)
            onKitDismissed.send { repo.restoreKit(kitItem.kit) }
        }
    }
}