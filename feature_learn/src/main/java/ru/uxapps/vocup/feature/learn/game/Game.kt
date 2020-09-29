package ru.uxapps.vocup.feature.learn.game

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.feature.learn.R

internal enum class Game(
    val id: Int,
    @StringRes val title: Int,
    @StringRes val desc: Int,
    val type: Type
) {
    WordToTranslation(
        1,
        R.string.word_to_translation_title,
        R.string.word_to_translation_desc,
        Type.Read
    ) {
        override fun getRequirements(words: List<Word>) = getWordsWithUniqueTransReq(words)
    },
    TranslationToWord(
        2,
        R.string.translation_to_word_title,
        R.string.translation_to_word_desc,
        Type.Read
    ) {
        override fun getRequirements(words: List<Word>) = getWordsWithUniqueTransReq(words)
    };

    enum class Type(@DrawableRes val icon: Int) {
        Read(R.drawable.ic_read)
    }

    abstract fun getRequirements(words: List<Word>): Pair<Int, Array<out Any>>?
}

private fun getWordsWithUniqueTransReq(words: List<Word>, reqSize: Int = 10): Pair<Int, Array<out Any>>? {
    val allTranslations = words.flatMap { it.translations }
    val wordsWithUniqueTrans = words.filter { word ->
        word.translations.any { trans ->
            allTranslations.count { it == trans } == 1
        }
    }
    return if (wordsWithUniqueTrans.size >= reqSize) null
    else R.string.words_with_unique_trans_req_pattern to arrayOf(reqSize, wordsWithUniqueTrans.size)
}