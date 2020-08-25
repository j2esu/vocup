package ru.uxapps.vocup.feature

import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun Toolbar.setNavAsBack() {
    setNavigationOnClickListener {
        (context as? FragmentActivity)?.onBackPressed()
    }
}

inline fun <reified T> Fragment.router(): T = (parentFragment ?: activity) as T