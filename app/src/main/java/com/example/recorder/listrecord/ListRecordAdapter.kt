package com.example.recorder.listrecord

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recorder.databinding.ListItemRecordBinding
import com.example.recorder.repository.database.Record
import java.util.concurrent.TimeUnit

class ListRecordAdapter : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {
    var data = listOf<Record>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordingItem = data[position]
        holder.bind(recordingItem)
    }

    override fun getItemCount() = data.size

    class ViewHolder private constructor(binding: ListItemRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var recordName: TextView = binding.textFileName
        var recordLength: TextView = binding.textFileLength

        fun bind(currentRecord: Record) {
            val recordDuration = currentRecord.length
            val minutes = TimeUnit.MILLISECONDS.toMinutes(recordDuration)
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(recordDuration) - TimeUnit.MINUTES.toSeconds(minutes)
            recordName.text = currentRecord.name
            recordLength.text = String.format("%02d:%02d", minutes, seconds)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemRecordBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}