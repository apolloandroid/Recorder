package com.example.recorder.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recording_table")
data class RecordingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name="name")
    var name: String = "",
    @ColumnInfo(name="file_path")
    var filePath: String = "",
    @ColumnInfo(name="length")
    var length: Long = 0L,
    @ColumnInfo(name="time")
    var time: Long = 0L
)