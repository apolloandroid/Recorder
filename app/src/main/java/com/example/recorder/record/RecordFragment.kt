package com.example.recorder.record


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.recorder.MainActivity
import com.example.recorder.R
import com.example.recorder.databinding.FragmentRecordBinding
import kotlinx.android.synthetic.main.fragment_record.*

class RecordFragment : Fragment() {

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var mainActivity: MainActivity
    private val MY_PERMISSIONS_RECORD_AUDIO = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentRecordBinding>(
            inflater,
            R.layout.fragment_record,
            container, false
        )
        recordViewModel = initRecordViewModel()

        mainActivity = activity as MainActivity

        binding.recordViewModel = recordViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        if (!isServicesRunning()) {
            recordViewModel.resetTimer()
        } else {
            binding.playButton.setImageResource(R.drawable.ic_stop)
        }

        binding.playButton.setOnClickListener {
            onPlayButtonClickListener()
        }

        createNotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        return binding.root
    }

    private fun onPlayButtonClickListener() {
        if (isPermissions()) {
            requestPermissions(
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_RECORD_AUDIO
            )
        } else {
            if (isServicesRunning()) {
                playButton.setImageResource(R.drawable.ic_microphone)
                recordViewModel.onRecord(false)
            } else {
                playButton.setImageResource(R.drawable.ic_stop)
                recordViewModel.onRecord(true)
                Toast.makeText(activity, R.string.toast_recording_start, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isPermissions(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED

    private fun isServicesRunning(): Boolean =
        mainActivity.isServiceRunning()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    recordViewModel.onRecord(true)
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.toast_recording_permissions), Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                    setSound(null, null)
                }
            val notificationManager =
                requireActivity().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun initRecordViewModel(): RecordViewModel {
        val application = requireNotNull(activity).application
        val recordViewModelFactory = RecordViewModelFactory(application)
        return recordViewModelFactory.create(RecordViewModel::class.java)
    }
}