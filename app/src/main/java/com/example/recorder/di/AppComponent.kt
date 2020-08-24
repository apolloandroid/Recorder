package com.example.recorder.di

import androidx.fragment.app.Fragment
import dagger.Component

@Component(modules = [RecordsFragmentModule::class])
interface AppComponent {
    fun injectRecordsFragment(recordsFragment: Fragment)
}