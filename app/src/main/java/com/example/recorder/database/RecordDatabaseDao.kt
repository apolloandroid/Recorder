package com.example.recorder.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDatabaseDao {
    @Insert
    fun insertRecord(record: RecordingItem)

    @Update
    fun updateRecord(record: RecordingItem)

    @Query("SELECT * FROM recording_table WHERE id = :id")
    fun getRecord(id: Long?): RecordingItem?

    @Query("DELETE FROM recording_table")
    fun clearAll()

    @Query("DELETE FROM recording_table WHERE id = :id")
    fun removeRecord(id: Long?)

    @Query("SELECT * FROM recording_table ORDER BY id DESC")
    fun getAllRecords():LiveData<List<RecordingItem>>

    @Query("SELECT COUNT(*) FROM recording_table")
    fun getCount():LiveData<Int>
}