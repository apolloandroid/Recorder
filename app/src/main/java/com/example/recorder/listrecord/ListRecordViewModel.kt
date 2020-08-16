package com.example.recorder.listrecord

import androidx.lifecycle.ViewModel
import com.example.recorder.database.RecordDatabaseDao

class ListRecordViewModel(dataSource: RecordDatabaseDao) : ViewModel() {
    val database = dataSource
    val records = database.getAllRecords()
}