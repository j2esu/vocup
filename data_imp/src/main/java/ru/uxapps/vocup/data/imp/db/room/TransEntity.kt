package ru.uxapps.vocup.data.imp.db.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Translations",
    foreignKeys = [
        ForeignKey(
            entity = WordEntity::class,
            parentColumns = ["id"],
            childColumns = ["wordId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal class TransEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val text: String,
    @ColumnInfo(index = true) val wordId: Long,
    val created: Long = System.nanoTime()
)
