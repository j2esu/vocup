package ru.uxapps.vocup.data.imp.api.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FrequentWords")
internal class FrequentWordsEntity(
    @PrimaryKey val id: Long,
    val text: String
)