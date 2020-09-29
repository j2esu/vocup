package ru.uxapps.vocup.data.imp.db.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface WordDao {

    @Transaction
    @Query("select * from Words order by created desc")
    fun getAllWords(): Flow<List<WordWithTranslations>>

    @Transaction
    @Query("select * from Words where id == :wordId")
    fun getWord(wordId: Long): Flow<WordWithTranslations?>

    @Insert
    suspend fun addWord(word: WordEntity): Long

    @Query("delete from Words where id == :wordId")
    suspend fun deleteWord(wordId: Long)

    @Query("update Words set progress = :progress where id == :wordId")
    suspend fun updateProgress(wordId: Long, progress: Int)

}

internal data class WordWithTranslations(
    @Embedded val word: WordEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "wordId"
    )
    val translations: List<TransEntity>
)