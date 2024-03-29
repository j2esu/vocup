package ru.uxapps.vocup.data.imp.api.local

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface FrequentWordsDao {

    @Query("select * from FrequentWords where lower(text) like lower(:input) || '%' order by text limit :limit")
    suspend fun findCompletions(input: String, limit: Int): List<FrequentWordsEntity>

}
