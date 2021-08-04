package ru.uxapps.vocup.data.imp.api.local

import androidx.room.Dao
import androidx.room.Query

@Dao
internal interface CmuDictDao {

    @Query("select * from CmuDict where lower(text) == lower(:wordText)")
    suspend fun findPron(wordText: String): List<CmuDictEntity>

}
