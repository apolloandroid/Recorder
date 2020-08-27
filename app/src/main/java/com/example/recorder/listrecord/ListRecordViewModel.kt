package com.example.recorder.listrecord

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.recorder.repository.Repository
import com.example.recorder.repository.database.Record
import com.example.recorder.repository.database.RecordDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking

class ListRecordViewModel(private val context: Context, private val repository: Repository) :
    ViewModel() {

     val records: LiveData<List<Record>> = getAllRecords()

    private fun getAllRecords(): LiveData<List<Record>> {
        return runBlocking { repository.getAllRecords() }
    }
}