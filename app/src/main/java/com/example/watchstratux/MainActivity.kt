// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.os.VibrationEffect
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.wear.ambient.AmbientModeSupport
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.watchstratux.databinding.ActivityMainBinding

class MainActivity : FragmentActivity(), AmbientModeSupport.AmbientCallbackProvider {

    lateinit var binding: ActivityMainBinding

    val mainRecyclerViewAdapter = MainRecyclerViewAdapter(this)

    lateinit var wakeLock: PowerManager.WakeLock
    lateinit var ambientController: AmbientModeSupport.AmbientController

    private val TAG = "WatchStratux MainActivity"

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return MyAmbientCallback()
    }
    private class MyAmbientCallback : AmbientModeSupport.AmbientCallback() {
        override fun onEnterAmbient(ambientDetails: Bundle) {
            // Handle entering ambient mode
        }

        override fun onExitAmbient() {
            // Handle exiting ambient mode
        }

        override fun onUpdateAmbient() {
            // Update the content
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppData.preferenceHandler = AppPreferenceHandler(this, "RadarPreferences", AppData.defaultPreferences.list)
        AppData.preferenceHandler.loadPreferences(AppData.preferences.list)

        AppData.vibrator = getSystemService(android.app.Activity.VIBRATOR_SERVICE) as android.os.Vibrator
        AppData.displayWidth = (getSystemService(WINDOW_SERVICE) as WindowManager).currentWindowMetrics.bounds.width().toFloat()
        AppData.displayHeight = (getSystemService(WINDOW_SERVICE) as WindowManager).currentWindowMetrics.bounds.height().toFloat()
        AppData.displayCenterWidth = AppData.displayWidth.div(2)
        AppData.displayCenterHeight = AppData.displayHeight.div(2)

        AppData.stratuxTcpReceiver = StratuxTcpReceiver()
        AppData.stratuxWsReceiver = StratuxWsReceiver()

        // AmbientModeSupport needed to keep app in focus after wake-up
        ambientController = AmbientModeSupport.attach(this)

        AppData.powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = AppData.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG)
        wakeLock.acquire()

        // start TCP and WebSocket receiver in a foreground service, in order not to be killed by doze mode
        startForegroundService(Intent(this, StratuxForegroundService::class.java))

        val mWearableRecyclerView = findViewById<WearableRecyclerView>(R.id.main_view)
        mWearableRecyclerView.layoutManager = WearableLinearLayoutManager(this, CustomScrollingLayoutCallback())

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(mWearableRecyclerView)

        // enable/disable updateRadarView when user change to another app
        mWearableRecyclerView.adapter = mainRecyclerViewAdapter
        mWearableRecyclerView.viewTreeObserver.addOnWindowFocusChangeListener { hasFocus ->
            if (hasFocus) {
                val pos = (mWearableRecyclerView.layoutManager as WearableLinearLayoutManager).getPosition(snapHelper.findSnapView(mWearableRecyclerView.layoutManager)!!)
                mainRecyclerViewAdapter.updateRadarView = (pos == 0)
                mainRecyclerViewAdapter.updateSystemInfo = (pos == 1)
            } else {
                mainRecyclerViewAdapter.updateRadarView = false
                mainRecyclerViewAdapter.updateSystemInfo = false
            }
        }

        // use onScrollStateChanged to snap item and to enable/disable updateRadarView if out of focus
        mWearableRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(mWearableRecyclerView.layoutManager)
                    val pos = (mWearableRecyclerView.layoutManager as WearableLinearLayoutManager).getPosition(centerView!!)
                    mainRecyclerViewAdapter.updateRadarView = (pos == 0)
                    mainRecyclerViewAdapter.updateSystemInfo = (pos == 1)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "Started with Version Name: "+BuildConfig.VERSION_NAME+" Build Number: "+BuildConfig.VERSION_CODE)
        if(BuildConfig.DEBUG) Log.i(TAG, "in debug mode")
        else Log.i(TAG, "in release mode")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG,"Resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG,"Paused")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG,"Stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG,"Destroyed")
        if( AppData.preferences.vibrationAlarm.value == true ) {
            AppData.vibrator.vibrate(VibrationEffect.createWaveform(AppData.alarmExitPattern, AppData.alarmExitTiming, -1))
        }
        stopService(Intent(this, StratuxForegroundService::class.java))
        mainRecyclerViewAdapter.handlerRadarView.removeCallbacks(mainRecyclerViewAdapter.runnableRadarView)
        mainRecyclerViewAdapter.handlerSystemInfo.removeCallbacks(mainRecyclerViewAdapter.runnableSystemInfo)
        wakeLock.release()
    }
}