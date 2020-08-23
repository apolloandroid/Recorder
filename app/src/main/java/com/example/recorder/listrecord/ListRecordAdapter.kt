package com.example.recorder.listrecord

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recorder.R
import com.example.recorder.repository.database.Record
import java.util.concurrent.TimeUnit

class ListRecordAdapter : RecyclerView.Adapter<ListRecordAdapter.ViewHolder>() {
    var data = listOf<Record>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var vName: TextView = itemView.findViewById(R.id.text_file_name)
        var vLength: TextView = itemView.findViewById(R.id.text_file_length)

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view: View = layoutInflater.inflate(R.layout.list_item_record, parent, false)
                return ViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount() = data.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val recordingItem = data[position]
        val itemDuration = recordingItem.length
        val minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(itemDuration) - TimeUnit.MINUTES.toSeconds(minutes)
        holder.vName.text = recordingItem.name
        holder.vLength.text = String.format("%02d:%02d", minutes, seconds)
    }
}