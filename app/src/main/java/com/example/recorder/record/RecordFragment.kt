package com.example.recorder.record

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.recorder.MainActivity
import com.example.recorder.R
import com.example.recorder.databinding.FragmentRecordBinding
import com.example.recorder.di.DaggerAppComponent
import com.example.recorder.di.RecordsFragmentModule
import javax.inject.Inject

class RecordFragment : Fragment() {
    @Inject
    lateinit var recordViewModel: RecordViewModel
    private lateinit var binding: FragmentRecordBinding
    private lateinit var mainActivity: MainActivity
    private val MY_PERMISSIONS_RECORD_AUDIO = 123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_record, container, false)
        injectFragment()

        mainActivity = activity as MainActivity

        binding.recordViewModel = recordViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initObservers()

        if (!mainActivity.isTimerRunning()) {
            recordViewModel.resetTimer()
        } else {
            setPlayButtonImageResource(false)
        }

        binding.buttonPlay.setOnClickListener { onPlayButtonClickListener() }

        return binding.root
    }

    private fun injectFragment() {
        val component = DaggerAppComponent.builder()
            .recordsFragmentModule(RecordsFragmentModule(activity?.applicationContext!!))
            .build()
        component?.injectRecordsFragment(this)
    }

    private fun initObservers(){
        recordViewModel.isPermission.observe(viewLifecycleOwner, Observer {
            if (!it) requestPermissions()
        })
    }

    private fun onPlayButtonClickListener() {
        if (!isPermissions()) {
            requestPermissions()
        } else {
            if (mainActivity.isTimerRunning()) {
                setPlayButtonImageResource(true)
                recordViewModel.stopRecord()
            } else {
                setPlayButtonImageResource(false)
                recordViewModel.startRecord()
                Toast.makeText(activity, R.string.toast_recording_start, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isPermissions(): Boolean =
        ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            MY_PERMISSIONS_RECORD_AUDIO
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_RECORD_AUDIO) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    activity, getString(R.string.toast_recording_permissions), Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
    }

    private fun setPlayButtonImageResource(isTimerRunning: Boolean) {
        when (isTimerRunning) {
            true -> binding.buttonPlay.setImageResource(R.drawable.ic_microphone)
            false -> binding.buttonPlay.setImageResource(R.drawable.ic_stop)
        }
    }
}