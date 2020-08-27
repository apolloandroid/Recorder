package com.example.recorder.listrecord

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.recorder.R
import com.example.recorder.databinding.FragmentListRecordBinding
import com.example.recorder.repository.RecordRepository
import com.example.recorder.repository.Repository


class ListRecordFragment : Fragment() {
    private val listRecordViewModel by lazy { initViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentListRecordBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_list_record, container, false)

        initViewModel()
        binding.listRecordViewModel = listRecordViewModel
        val adapter = ListRecordAdapter()
        binding.listRecords.adapter = adapter

        listRecordViewModel.records.observe(viewLifecycleOwner, Observer {
            it.let { adapter.data = it }
        })
        return binding.root
    }

    private fun initViewModel(): ListRecordViewModel {
        val repository: Repository = RecordRepository.getInstance(activity?.applicationContext!!)
        val viewModelFactory =
            ListRecordViewModelFactory(activity?.applicationContext!!, repository)
        return viewModelFactory.create(ListRecordViewModel::class.java)
    }
}