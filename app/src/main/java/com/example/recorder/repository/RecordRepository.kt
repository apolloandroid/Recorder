package com.example.recorder.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.recorder.repository.database.Record
import com.example.recorder.repository.database.RecordDatabase

class RecordRepository private constructor(context: Context) : Repository {

    private val recordsDatabaseDao = RecordDatabase.getInstance(context).recordDatabaseDao

    companion object {
        fun getInstance(context: Context): Repository {
            var instance: Repository? = null
            if (instance == null) {
                instance = RecordRepository(context)
            }
            return instance
        }
    }

    override suspend fun insertRecord(record: Record) {
        recordsDatabaseDao.insertRecord(record)
    }

    override suspend fun updateRecord(record: Record) {
        recordsDatabaseDao.updateRecord(record)
    }

    override suspend fun getRecord(id: Long?): Record? {
        return recordsDatabaseDao.getRecord(id)
    }

    override suspend fun clearAllRecords() {
        recordsDatabaseDao.clearAllRecords()
    }

    override suspend fun removeRecord(id: Long?) {
        recordsDatabaseDao.removeRecord(id)
    }

    override suspend fun getAllRecords(): LiveData<List<Record>> {
        return recordsDatabaseDao.getAllRecords()
    }

    override suspend fun getCountRecords(): LiveData<Int> {
        return recordsDatabaseDao.getCountRecords()
    }
}