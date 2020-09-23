package ru.uxapps.vocup.feature.learn.game

import androidx.annotation.StringRes
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.R

internal enum class Game(
    @StringRes val title: Int,
    @StringRes val desc: Int
) {
    WordToTranslation(
        R.string.word_to_translation_title,
        R.string.word_to_translation_desc
    ) {
        override fun getRequirements(words: List<Word>): Pair<Int, Array<out Any>>? {
            val wordsWithTrans = words.filter { it.translations.isNotEmpty() }
            return if (wordsWithTrans.size >= 10) null
            else R.string.word_to_translation_req_pattern to arrayOf(wordsWithTrans.size)
        }
    };

    abstract fun getRequirements(words: List<Word>): Pair<Int, Array<out Any>>?
}