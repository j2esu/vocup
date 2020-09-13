package ru.uxapps.vocup.transition

import android.content.Context
import androidx.annotation.TransitionRes
import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionInflater

fun Fragment.loadTransition(@TransitionRes transRes: Int): Transition =
    requireContext().loadTransition(transRes)

fun Context.loadTransition(@TransitionRes transRes: Int): Transition =
    TransitionInflater.from(this).inflateTransition(transRes)