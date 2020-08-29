package com.example.recorder.record

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.example.recorder.MainActivity
import com.example.recorder.R
import com.example.recorder.repository.RecordRepository
import com.example.recorder.repository.Repository
import com.example.recorder.repository.database.Record
import kotlinx.coroutines.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class RecordViewModel(private val context: Context) : ViewModel() {
    private lateinit var timer: CountDownTimer
    private val channelId = context.getString(R.string.notification_channel_id)
    private val channelName = context.getString(R.string.notification_channel_name)
    private val TRIGGER_TIME = "TRIGGER_AT"
    private val second: Long = 1_000L
    private val prefs = context.getSharedPreferences("com.example.recorder", Context.MODE_PRIVATE)

    private var _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String>
        get() = _elapsedTime

    private var _isPermissions = MutableLiveData<Boolean>(false)
    val isPermission: LiveData<Boolean>
        get() = _isPermissions

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                    setSound(null, null)
                }
            val notificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun startRecord() {
        val intent = Intent(context, RecordService::class.java)
        makeDirectoryForSavingRecord()
        context.startService(intent)
        prepareTimer()
        startTimer()
    }

    fun stopRecord() {
        val intent = Intent(context, RecordService::class.java)
        context.stopService(intent)
        stopTimer()
        resetTimer()
    }

    private fun makeDirectoryForSavingRecord() {
        val folder = File(context.getExternalFilesDir(null)?.absolutePath.toString() + "/Recorder")
        if (!folder.exists()) folder.mkdir()
    }

    private fun prepareTimer() {
        val triggerTime = SystemClock.elapsedRealtime()
        viewModelScope.launch {
            saveTime(triggerTime)
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            val triggerTime = loadTime()
            timer = object : CountDownTimer(triggerTime, second) {
                override fun onTick(millisUntilFinished: Long) {
                    _elapsedTime.value = timeFormatter(SystemClock.elapsedRealtime() - triggerTime)
                }

                override fun onFinish() {
                    resetTimer()
                }
            }
            timer.start()
        }
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) timer.cancel()
    }

    fun resetTimer() {
        _elapsedTime.value = timeFormatter(0)
        viewModelScope.launch { saveTime(0) }
    }

    fun timeFormatter(time: Long): String {
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time) % 60,
            TimeUnit.MILLISECONDS.toMinutes(time) % 60,
            TimeUnit.MILLISECONDS.toSeconds(time) % 60
        )
    }

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }
}
