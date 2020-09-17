package ru.uxapps.vocup.data.imp.db.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "Words")
internal data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val text: String,
    val pron: String?,
    val progress: Int = Random.nextInt(100),
    val created: Long = System.nanoTime()
)