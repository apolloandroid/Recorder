package com.example.recorder.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.recorder.repository.database.Record
import com.example.recorder.repository.database.RecordDatabase

class RecordsRepository private constructor(application: Application) : Repository {

    private val recordsDatabaseDao = RecordDatabase.getInstance(application).recordDatabaseDao

    companion object {
        fun getInstance(application: Application): Repository {
            var instance: Repository? = null
            if (instance == null) {
                instance = RecordsRepository(application)
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