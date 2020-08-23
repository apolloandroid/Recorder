package com.example.recorder.listrecord

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.recorder.R
import com.example.recorder.repository.database.RecordDatabase
import com.example.recorder.databinding.FragmentListRecordBinding


class ListRecordFragment : Fragment() {

    @SuppressLint("FragmentLiveDataObserve")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentListRecordBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_record, container, false)
        val application = requireNotNull(this.activity).application
        val datasource = RecordDatabase.getInstance(application).recordDatabaseDao
        val viewModelFactory = ListRecordViewModelFactory(datasource)
        val listRecordViewModel = viewModelFactory.create(ListRecordViewModel::class.java)
        binding.listRecordViewModel = listRecordViewModel
        val adapter = ListRecordAdapter()
        binding.listRecords.adapter = adapter
        listRecordViewModel.records.observe(this, Observer {
            it.let { adapter.data = it }
        })
        return binding.root
    }
}