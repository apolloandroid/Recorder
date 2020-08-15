package com.example.recorder.record

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.recorder.MainActivity
import com.example.recorder.R
import com.example.recorder.database.RecordDatabase
import com.example.recorder.database.RecordDatabaseDao
import com.example.recorder.database.RecordingItem
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat

class RecordService : Service() {
    private var mFileName: String? = null
    private var mFilePath: String? = null
    private var mCountRecords: Int? = null
    private var mRecorder: MediaRecorder? = null
    private var mStartingTimeMillis: Long = 0
    private var mElapsedTimeMillis: Long = 0
    private var mDatabase: RecordDatabaseDao? = null
    private val mJob = Job()
    private val mUiScope = CoroutineScope(Dispatchers.Main + mJob)

    override fun onCreate() {
        super.onCreate()
        mDatabase = RecordDatabase.getInstance(applicationContext).recordDatabaseDao
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        mCountRecords = intent?.extras?.get("COUNT") as Int
        return START_NOT_STICKY
    }

    private fun startRecording() {
        setFileNameAndPath()
        mRecorder = MediaRecorder()
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mRecorder?.setOutputFile(mFilePath)
        mRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mRecorder?.setAudioChannels(1)
        mRecorder?.setAudioEncodingBitRate(192000)

        try {
            mRecorder?.prepare()
            mRecorder?.start()
            mStartingTimeMillis = System.currentTimeMillis()
            startForeground(1, createNotification())
        } catch (e: Exception) {
            Log.e("RecordService", "Prepare failed")
        }
    }

    private fun createNotification(): Notification? {
        val mBuilder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_microphone)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getText(R.string.notification_recording))
            .setOngoing(true)
            .setContentIntent(
                PendingIntent.getActivities(
                    applicationContext, 0, arrayOf(
                        Intent(
                            applicationContext, MainActivity::class.java
                        )
                    ), 0
                )
            )
        return mBuilder.build()
    }

    private fun setFileNameAndPath() {
        var count = 0
        var file: File
        val dateTime = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())

        do {
            mFileName = "${getString(R.string.default_file_name)}_$dateTime$count.mp4"
            mFilePath = application.getExternalFilesDir(null)?.absolutePath
            mFilePath += "/$mFileName"
            count++
            file = File(mFilePath)
        } while (file.exists() && !file.isDirectory)
    }

    private fun stopRecording() {
        val recordingItem = RecordingItem()

        mRecorder?.stop()
        mElapsedTimeMillis = System.currentTimeMillis() - mStartingTimeMillis
        mRecorder?.release()
        Toast.makeText(this, getString(R.string.toast_recording_finish), Toast.LENGTH_SHORT).show()

        recordingItem.name = mFileName.toString()
        recordingItem.filePath = mFilePath.toString()
        recordingItem.length = mElapsedTimeMillis
        recordingItem.time = System.currentTimeMillis()

        mRecorder = null

        try {
            mUiScope.launch {
                withContext(Dispatchers.IO) {
                    mDatabase?.insertRecord(recordingItem)
                }
            }
        } catch (e: Exception) {
            Log.e("RecordService", "exception", e)
        }
    }

    override fun onDestroy() {
        if (mRecorder != null) {
            stopRecording()
        }
        super.onDestroy()
    }
}