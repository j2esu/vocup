package ru.uxapps.vocup.data.imp.db

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import ru.uxapps.vocup.data.api.Word
import ru.uxapps.vocup.data.imp.db.room.TransEntity
import ru.uxapps.vocup.data.imp.db.room.WordEntity
import ru.uxapps.vocup.data.imp.db.room.WordWithTranslations
import ru.uxapps.vocup.data.imp.db.room.WordsDb

class DbImp(context: Context) : Db {

    private val db = Room.databaseBuilder(context, WordsDb::class.java, "words.db")
        .fallbackToDestructiveMigration()
        .build()

    override fun getAllWords(): Flow<List<Word>> {
        return db.words().getAllWords().map { words ->
            words.map { it.toWord() }
        }
    }

    override fun getWord(wordId: Long): Flow<Word?> {
        return db.words().getWord(wordId).mapNotNull { it?.toWord() }
    }

    override suspend fun addWord(text: String, trans: List<String>, pron: String?) {
        db.withTransaction {
            val wordId = db.words().addWord(WordEntity(0, text, pron))
            db.trans().addTranslations(trans.map {
                TransEntity(0, it, wordId)
            })
        }
    }

    override suspend fun restoreWord(word: Word) {
        db.withTransaction {
            db.words().addWord(word.toEntity())
            db.trans().addTranslations(word.translations.map { it.toTransEntity(word.id) })
        }
    }

    override suspend fun deleteWord(wordId: Long) {
        db.words().deleteWord(wordId)
    }

    override suspend fun updateTranslations(wordId: Long, trans: List<String>) {
        db.trans().updateTranslations(wordId, trans.map { it.toTransEntity(wordId) })
    }

    override suspend fun updateProgress(wordId: Long, progress: Int) {
        db.words().updateProgress(wordId, progress)
    }

    private fun Word.toEntity() = WordEntity(id, text, pron, progress, created)
    private fun String.toTransEntity(wordId: Long) = TransEntity(0, this, wordId)
    private fun TransEntity.toTrans() = text
    private fun WordWithTranslations.toWord() = Word(
        word.id, word.text, translations.sortedBy { it.created }.map { it.toTrans() },
        word.pron, word.progress, word.created
    )
}