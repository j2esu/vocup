package ru.uxapps.vocup.data.imp.db.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WordEntity::class, TransEntity::class], version = 1, exportSchema = false)
internal abstract class WordsDb : RoomDatabase() {
    abstract fun words(): WordDao
    abstract fun trans(): TransDao
}