package ru.uxapps.vocup.data.imp.db.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal abstract class TransDao {

    @Transaction
    @Insert
    abstract suspend fun addTranslations(trans: List<TransEntity>)

    @Query("delete from Translations where wordId == :wordId")
    abstract suspend fun deleteTranslations(wordId: Long)

    @Transaction
    open suspend fun updateTranslations(wordId: Long, trans: List<TransEntity>) {
        deleteTranslations(wordId)
        addTranslations(trans)
    }
}
