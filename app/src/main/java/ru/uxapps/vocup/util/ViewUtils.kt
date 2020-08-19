package ru.uxapps.vocup.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

fun <T : ViewBinding> ViewGroup.inflateBind(inflateFunc: (LayoutInflater, ViewGroup, Boolean) -> T) =
    inflateFunc(LayoutInflater.from(context), this, false)