package ru.uxapps.vocup.feature

import androidx.annotation.TransitionRes
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull


inline fun Fragment.delayTransition(
    vararg data: LiveData<*>, timeout: Long = 500,
    crossinline onDataLoaded: () -> Unit = {}
) {
    postponeEnterTransition()
    lifecycleScope.launch {
        withTimeoutOrNull(timeout) {
            combine(data.map { it.asFlow() }) {}.first()
        }
        onDataLoaded()
        view?.doOnPreDraw {
            startPostponedEnterTransition()
        }
        view?.invalidate()
    }
}

fun Fragment.loadTransition(@TransitionRes transRes: Int): Transition =
    TransitionInflater.from(requireContext()).inflateTransition(transRes)