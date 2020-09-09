package ru.uxapps.vocup.feature

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.TransitionRes
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun Fragment.delayTransition(timeout: Long = 500, await: suspend () -> Unit) {
    postponeEnterTransition()
    lifecycleScope.launch {
        withTimeoutOrNull(timeout) {
            await()
            delay(100) // workaround to let things like async submit list finish
        }
        (view?.parent as? ViewGroup)?.let {
            it.doOnPreDraw {
                startPostponedEnterTransition()
            }
            it.invalidate()
        }
    }
}

suspend fun BaseFragment.awaitReady() {
    suspendCoroutine<Unit> {
        doOnReadyForTransition { it.resume(Unit) }
    }
}

suspend fun LiveData<*>.awaitValue() {
    asFlow().first()
}

fun Fragment.loadTransition(@TransitionRes transRes: Int): Transition =
    requireContext().loadTransition(transRes)

fun Context.loadTransition(@TransitionRes transRes: Int): Transition =
    TransitionInflater.from(this).inflateTransition(transRes)