package ru.uxapps.vocup.data.imp.api.local

import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import ru.uxapps.vocup.data.imp.R

internal enum class KitRes(
    val id: Long,
    @StringRes val title: Int,
    @ArrayRes val words: Int
) {
    Top1(1, R.string.top_1_title, R.array.top_1_words),
    Top2(2, R.string.top_2_title, R.array.top_2_words)
}