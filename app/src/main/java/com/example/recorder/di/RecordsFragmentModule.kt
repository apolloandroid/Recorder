package com.example.recorder.di

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.recorder.record.RecordViewModel
import com.example.recorder.record.RecordViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class RecordsFragmentModule(private val context: Context) {
    @Provides
    fun provideRecordsViewModel(): RecordViewModel {
        val recordViewModelFactory = RecordViewModelFactory(context)
        return recordViewModelFactory.create(RecordViewModel::class.java)
    }
}