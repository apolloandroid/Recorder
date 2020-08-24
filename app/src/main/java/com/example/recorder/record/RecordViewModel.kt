package com.example.recorder.record

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.recorder.R
import com.example.recorder.repository.RecordsRepository
import com.example.recorder.repository.Repository
import kotlinx.android.synthetic.main.fragment_record.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

class RecordViewModel(private val context: Context) : ViewModel() {

    private val repository: Repository = RecordsRepository.getInstance(context)
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

    private lateinit var timer: CountDownTimer


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

    fun onRecord(start: Boolean) {
        val intent = Intent(context, RecordService::class.java)
        if (start) {
            val folder =
                File(context.getExternalFilesDir(null)?.absolutePath.toString() + "/Recorder")
            if (!folder.exists()) {
                folder.mkdir()
            }
            context.startService(intent)
            startTimer()
        } else {
            context.stopService(intent)
            stopTimer()
        }
    }

    fun timeFormatter(time: Long): String {
        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(time) % 60,
            TimeUnit.MILLISECONDS.toMinutes(time) % 60,
            TimeUnit.MILLISECONDS.toSeconds(time) % 60
        )
    }

    private fun stopTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
        }
        resetTimer()
    }

    private fun startTimer() {
        val triggerTime = SystemClock.elapsedRealtime()
//        change to custom scope
        viewModelScope.launch {
            saveTime(triggerTime)
            createTimer()
        }
    }

    fun resetTimer() {
        _elapsedTime.value = timeFormatter(0)
        //        change to custom scope
        viewModelScope.launch {
            saveTime(0)
        }
    }

    private fun createTimer() {
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

    private suspend fun saveTime(triggerTime: Long) =
        withContext(Dispatchers.IO) {
            prefs.edit().putLong(TRIGGER_TIME, triggerTime).apply()
        }

    private suspend fun loadTime(): Long =
        withContext(Dispatchers.IO) {
            prefs.getLong(TRIGGER_TIME, 0)
        }
}







