package ru.uxapps.vocup.data.imp.api.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CmuDictEntity::class, FrequentWordsEntity::class], version = 1, exportSchema = false)
internal abstract class LocalApiDb : RoomDatabase() {
    abstract fun dict(): CmuDictDao
    abstract fun frequentWords(): FrequentWordsDao
}
