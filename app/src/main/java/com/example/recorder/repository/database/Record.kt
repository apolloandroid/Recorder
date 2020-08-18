package com.example.recorder.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
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