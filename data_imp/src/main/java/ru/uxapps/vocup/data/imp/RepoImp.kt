package ru.uxapps.vocup.data.imp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import ru.uxapps.vocup.data.api.Def
import ru.uxapps.vocup.data.api.Language
import ru.uxapps.vocup.data.api.Repo
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.data.imp.api.Api
import ru.uxapps.vocup.data.imp.db.Db
import java.io.IOException

class RepoImp(
    context: Context,
    private val db: Db,
    private val api: Api
) : Repo {

    companion object {
        private const val PREFS_NAME = "vocup.prefs"
        private const val KEY_LANG = "lang"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getAllWords(): Flow<List<Word>> = db.getAllWords()
    override fun getWord(wordId: Long): Flow<Word?> = db.getWord(wordId)

    override fun getTargetLanguage(): Flow<Language> = callbackFlow<String> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> sendBlocking(key) }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.filter { it == KEY_LANG }.map { currentLang }.onStart { emit(currentLang) }

    private var currentLang: Language
        get() {
            return prefs.getString(KEY_LANG, null)?.let { code ->
                Language.values().find { it.code == code }
            } ?: suggestTargetLang()
        }
        set(value) {
            prefs.edit().putString(KEY_LANG, value.code).apply()
        }

    private fun suggestTargetLang(): Language {
        val userLocales = LocaleListCompat.getAdjustedDefault()
        for (i in 0 until userLocales.size()) {
            val supportedLang = Language.values().find { it.code == userLocales[i].language }
            if (supportedLang != null) {
                return supportedLang
            }
        }
        return Language.Russian
    }

    override suspend fun getDefinitions(word: String, lang: Language): List<Def> {
        val predictions = try {
            api.getPredictions(word, lang).take(2)
        } catch (e: IOException) {
            emptyList<String>()
        }
        return api.getDefinitions((listOf(word) + predictions).distinct(), lang)
    }

    override suspend fun getCompletions(word: String): List<String> = api.getCompletions(word)
    override suspend fun addWord(text: String, trans: List<String>) =
        db.addWord(text, trans, api.getPronunciations(text).firstOrNull())

    override suspend fun restoreWord(word: Word) = db.restoreWord(word)
    override suspend fun deleteWord(wordId: Long) = db.deleteWord(wordId)

    override suspend fun setTargetLanguage(lang: Language) {
        currentLang = lang
    }

    override suspend fun updateTranslations(wordId: Long, trans: List<String>) =
        db.updateTranslations(wordId, trans)

    override suspend fun addTranslations(wordId: Long, trans: List<String>) {
        db.getWord(wordId).first()?.translations?.let { currentTrans ->
            db.updateTranslations(wordId, currentTrans + trans)
        }
    }
}