package com.example.recorder.di

import com.example.recorder.record.RecordFragment
import com.example.recorder.record.RecordViewModel
import dagger.Component

@Component(modules = [RecordsFragmentModule::class])
interface AppComponent {

    fun injectRecordsFragment(recordsFragment: RecordFragment)
}