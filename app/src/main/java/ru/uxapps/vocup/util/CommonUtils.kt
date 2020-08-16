package ru.uxapps.vocup.util

fun <T> List<T>.move(from: Int, to: Int): List<T> = toMutableList().apply {
    add(if (from < to) to + 1 else to, get(from))
    removeAt(if (from < to) from else from + 1)
}