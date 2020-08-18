package com.example.recorder.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecordDatabaseDao {
    @Insert
    fun insertRecord(record: Record)

    @Update
    fun updateRecord(record: Record)

    @Query("SELECT * FROM records WHERE id = :id")
    fun getRecord(id: Long?): Record?

    @Query("DELETE FROM records")
    fun clearAllRecords()

    @Query("DELETE FROM records WHERE id = :id")
    fun removeRecord(id: Long?)

    @Query("SELECT * FROM records ORDER BY id DESC")
    fun getAllRecords(): LiveData<List<Record>>

    @Query("SELECT COUNT(*) FROM records")
    fun getCountRecords(): LiveData<Int>
}