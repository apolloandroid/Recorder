package com.example.recorder.listrecord

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.recorder.repository.Repository
import java.lang.IllegalArgumentException

class ListRecordViewModelFactory(private val context: Context, private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListRecordViewModel::class.java)) {
            return ListRecordViewModel(context, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}