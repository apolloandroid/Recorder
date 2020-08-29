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
import com.example.recorder.repository.RecordRepository
import com.example.recorder.repository.Repository
import com.example.recorder.repository.database.Record
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat

class RecordService : Service() {
    private val AUDIO_ENCODING_BITRATE = 192000
    private lateinit var repository: Repository
    private var fileName: String? = null
    private var filePath: String? = null
    private var recorder: MediaRecorder? = null
    private var startingTimeMillis: Long = 0
    private var elapsedTimeMillis: Long = 0
    private val recordServiceScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate() {
        super.onCreate()
        repository = RecordRepository.getInstance(application)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecording()
        return START_NOT_STICKY
    }

    private fun startRecording() {
        setFileNameAndPath()
        createRecorder()

        try {
            recorder?.prepare()
            recorder?.start()
            startingTimeMillis = System.currentTimeMillis()
            startForeground(1, createNotification())
        } catch (e: Exception) {
            Log.e("RecordService", "Prepare failed")
        }
    }

    private fun createRecorder() {
        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder?.setOutputFile(filePath)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder?.setAudioChannels(1)
        recorder?.setAudioEncodingBitRate(AUDIO_ENCODING_BITRATE)
    }

    private fun createNotification(): Notification? {
        val builder = NotificationCompat.Builder(
            applicationContext,
            getString(R.string.notification_channel_id)
        )
            .setSmallIcon(R.drawable.ic_microphone)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getText(R.string.notification_recording))
            .setOngoing(true)
        builder.setContentIntent(
            PendingIntent.getActivities(
                applicationContext, 0, arrayOf(
                    Intent(
                        applicationContext, MainActivity::class.java
                    )
                ), 0
            )
        )
        return builder.build()
    }

    private fun setFileNameAndPath() {
        var count = 0
        var file: File
        val dateTime =
            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(System.currentTimeMillis())

        do {
            fileName = "${getString(R.string.default_file_name)}_$dateTime$count.mp4"
            filePath = application.getExternalFilesDir(null)?.absolutePath
            filePath += "/$fileName"
            count++
            file = File(filePath)
        } while (file.exists() && !file.isDirectory)
    }

    private fun stopRecording() {
        recorder?.stop()
        val record = createRecordingItem()
        recorder?.release()
        recorder = null

        saveRecord(record)
        Toast.makeText(this, getString(R.string.toast_recording_finish), Toast.LENGTH_SHORT)
            .show()
    }

    private fun createRecordingItem(): Record {
        elapsedTimeMillis = System.currentTimeMillis() - startingTimeMillis
        val recordingItem = Record()
        recordingItem.name = fileName.toString()
        recordingItem.filePath = filePath.toString()
        recordingItem.length = elapsedTimeMillis
        recordingItem.time = System.currentTimeMillis()
        return recordingItem
    }

    private fun saveRecord(record:Record) {
        try {
            recordServiceScope.launch {
                repository.insertRecord(record)
            }
        } catch (e: Exception) {
            Log.e("RecordService", "exception", e)
        }
    }

    override fun onDestroy() {
        if (recorder != null) {
            stopRecording()
        }
        super.onDestroy()
    }
}