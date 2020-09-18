package ru.uxapps.vocup.feature.learn.model

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import ru.uxapps.vocup.feature.learn.R

internal interface Exercises {
    val exercises: LiveData<List<ExerciseItem>>
}

internal enum class Exercise(
    @StringRes val title: Int,
    @StringRes val desc: Int,
    @StringRes val requirement: Int
) {
    WordToTranslation(
        R.string.word_to_translation_title,
        R.string.word_to_translation_desc,
        R.string.word_to_translation_req
    )
}

internal data class ExerciseItem(val exercise: Exercise, val enabled: Boolean)