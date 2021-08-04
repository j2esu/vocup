package ru.uxapps.vocup.data.imp.api.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CmuDict")
internal data class CmuDictEntity(
    @PrimaryKey val id: Long,
    val text: String,
    val pron: String
)
