package ru.uxapps.vocup.feature

import android.view.View

interface SoftInput {
    fun show(view: View)
    fun hide()
    var nextShowDelay: Long
}

interface SoftInputProvider {
    val softInput: SoftInput
}

val View.softInput: SoftInput get() = (context as SoftInputProvider).softInput