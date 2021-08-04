package ru.uxapps.vocup.data.imp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.math.MathUtils
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import ru.uxapps.vocup.data.api.*
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
        private const val KEY_DISMISSED_KITS = "dismissed_kits"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun getAllWords(): Flow<List<Word>> = db.getAllWords()
    override fun getWord(wordId: Long): Flow<Word?> = db.getWord(wordId)

    override fun getTargetLanguage(): Flow<Language> = callbackFlow<String> {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key -> trySendBlocking(key) }
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

    private var dismissedKits: Set<Long>
        get() = prefs.getStringSet(KEY_DISMISSED_KITS, null)?.map { it.toLong() }?.toSet() ?: emptySet()
        set(value) {
            prefs.edit().putStringSet(KEY_DISMISSED_KITS, value.map { it.toString() }.toSet()).apply()
        }

    private val dismissedKitsChanged = Channel<Unit>()

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

    override suspend fun getDefinitions(word: String): List<Def> {
        val predictions = try {
            api.getPredictions(word, currentLang)
        } catch (e: IOException) {
            emptyList()
        }
        return api.getDefinitions((listOf(word) + predictions).distinct(), currentLang).sortedBy {
            !it.text.equals(word, true) && it.translations.isEmpty()
        }
    }

    override suspend fun getDefinitions(words: List<String>): List<Def> =
        words.chunked(10).flatMap { api.getDefinitions(it, currentLang) }

    override suspend fun getCompletions(word: String): List<String> = api.getCompletions(word)
    override suspend fun addWord(text: String, trans: List<String>) =
        db.addWord(text, trans, api.getPronunciations(text).firstOrNull())

    override suspend fun restoreWord(word: Word) = db.restoreWord(word)
    override suspend fun deleteWord(wordId: Long) = db.deleteWord(wordId)

    override suspend fun setTargetLanguage(lang: Language) {
        currentLang = lang
    }

    override suspend fun setTranslations(wordId: Long, trans: List<String>) =
        db.updateTranslations(wordId, trans)

    override suspend fun addTranslations(wordId: Long, trans: List<String>) {
        db.getWord(wordId).first()?.translations?.let { currentTrans ->
            db.updateTranslations(wordId, currentTrans + trans)
        }
    }

    override suspend fun updateProgress(word: Word, progressDiff: Int) =
        db.updateProgress(word.id, MathUtils.clamp(word.progress + progressDiff, 0, 100))

    override suspend fun getWordKits(): Flow<List<Kit>> {
        return dismissedKitsChanged.receiveAsFlow().onStart { emit(Unit) }
            .mapLatest {
                val dismissed = dismissedKits
                api.getWordKits(currentLang).filterNot { dismissed.contains(it.id) }
            }
    }

    override suspend fun dismissKit(kit: Kit) {
        dismissedKits = dismissedKits + kit.id
        dismissedKitsChanged.trySend(Unit)
    }

    override suspend fun restoreKit(kit: Kit) {
        dismissedKits = dismissedKits - kit.id
        dismissedKitsChanged.trySend(Unit)
    }
}
