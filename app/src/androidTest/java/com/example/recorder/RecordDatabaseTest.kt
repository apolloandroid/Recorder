package com.example.recorder

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.recorder.database.RecordDatabase
import com.example.recorder.database.RecordDatabaseDao
import com.example.recorder.database.RecordingItem
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class RecordDatabaseTest {
    private lateinit var recordDatabaseDao: RecordDatabaseDao
    private lateinit var recordDatabase: RecordDatabase

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().context
        recordDatabase = Room.inMemoryDatabaseBuilder(context, RecordDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        recordDatabaseDao = recordDatabase.recordDatabaseDao
    }

    @After
    fun closeDatabase() {
        recordDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun testDatabase() {
        recordDatabaseDao.insertRecord(RecordingItem())
        val getCount = recordDatabaseDao.getCount()
        Assert.assertEquals(getCount, 1)
    }
}