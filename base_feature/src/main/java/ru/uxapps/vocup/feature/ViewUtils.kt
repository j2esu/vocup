package ru.uxapps.vocup.feature

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding

fun <T : ViewBinding> ViewGroup.inflateBind(inflateFunc: (LayoutInflater, ViewGroup, Boolean) -> T) =
    inflateFunc(LayoutInflater.from(context), this, false)

fun Toolbar.setNavAsBack() {
    setNavigationOnClickListener {
        (context as? FragmentActivity)?.onBackPressed()
    }
}

fun Context.getColorAttr(@AttrRes colorAttr: Int): Int {
    val value = TypedValue()
    theme.resolveAttribute(colorAttr, value, true)
    return value.data
}