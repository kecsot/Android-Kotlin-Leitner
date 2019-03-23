package com.kecsot.leitnerbox.repository.database.local.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.database.local.room.converter.ImageItemTypeConverter
import com.kecsot.leitnerbox.repository.database.local.room.converter.LeitnerBoxRulesTypeConverter
import com.kecsot.leitnerbox.repository.database.local.room.dao.CardDao
import com.kecsot.leitnerbox.repository.database.local.room.dao.DeckDao
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity

@Database(entities = [DeckEntity::class, CardEntity::class], version = 1, exportSchema = false)
@TypeConverters(
    ImageItemTypeConverter::class,
    LeitnerBoxRulesTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {


    abstract fun getCardDao(): CardDao

    abstract fun getDeckDao(): DeckDao

    companion object {
        public const val DATABASE_FILENAME = "application.db"

        private var INSTANCE: AppDatabase? = null

        fun getInstance(): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        LeitnerBoxApplication.instance.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_FILENAME
                    )
                        .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            if (INSTANCE?.isOpen == true) {
                INSTANCE?.close()
            }
            INSTANCE = null
        }

    }
}