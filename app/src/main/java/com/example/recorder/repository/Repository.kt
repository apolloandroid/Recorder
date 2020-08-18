package com.example.recorder.repository

import androidx.lifecycle.LiveData
import com.example.recorder.repository.database.Record

interface Repository {

    suspend fun insertRecord(record: Record)

    suspend fun updateRecord(record: Record)

    suspend fun getRecord(id: Long?): Record?

    suspend fun clearAllRecords()

    suspend fun removeRecord(id: Long?)

    suspend fun getAllRecords(): LiveData<List<Record>>

    suspend fun getCountRecords(): LiveData<Int>
}