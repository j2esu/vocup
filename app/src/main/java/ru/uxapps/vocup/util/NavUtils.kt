package ru.uxapps.vocup.util

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun Toolbar.setNavAsBack() {
    setNavigationOnClickListener {
        (context as? FragmentActivity)?.onBackPressed()
    }
}

fun Fragment.back() = requireActivity().onBackPressed()
inline fun <reified T> Fragment.router(): T = (parentFragment ?: activity) as T
inline fun <reified T> Fragment.target(): T = (targetFragment ?: parentFragment ?: activity) as T