package com.example.recorder.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Record::class], version = 2, exportSchema = false)
abstract class RecordDatabase : RoomDatabase() {
    abstract val recordDatabaseDao: RecordDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: RecordDatabase? = null

        fun getInstance(context: Context): RecordDatabase {
            synchronized(this) {
                var instance =
                    INSTANCE
                if (instance == null) {
                    instance =
                        Room.databaseBuilder(
                            context.applicationContext, RecordDatabase::class.java,
                            "records")
                            .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}