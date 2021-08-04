package ru.uxapps.vocup.util

import androidx.fragment.app.Fragment

inline fun <reified T> Fragment.host(): T = (parentFragment ?: activity) as T

fun Fragment.forEachParentRecursive(action: (Fragment) -> Unit) {
    parentFragment?.let {
        action(it)
        it.forEachParentRecursive(action)
    }
}
