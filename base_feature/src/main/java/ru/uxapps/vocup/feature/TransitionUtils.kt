package ru.uxapps.vocup.feature

import android.content.Context
import androidx.annotation.TransitionRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun BaseFragment.awaitReady() {
    suspendCancellableCoroutine<Unit> {
        doOnReadyForTransition {
            it.resume(Unit)
        }
    }
}

suspend fun LiveData<*>.awaitValue() {
    asFlow().first()
}

fun Fragment.loadTransition(@TransitionRes transRes: Int): Transition =
    requireContext().loadTransition(transRes)

fun Context.loadTransition(@TransitionRes transRes: Int): Transition =
    TransitionInflater.from(this).inflateTransition(transRes)