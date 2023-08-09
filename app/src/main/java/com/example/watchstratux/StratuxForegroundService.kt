// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.wifi.WifiManager
import android.os.IBinder
import android.os.VibrationEffect
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StratuxForegroundService : Service() {

    val TAG = "StratuxForegroundService"

    var TCPisRunning = false
    var WSisRunning = false

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) Log.i(TAG, "Service created!")

        val channelID = createNotificationChannel(this)
        val channelBuilder = NotificationCompat.Builder(this, channelID)
        val notification = channelBuilder
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentTitle("Stratux Foreground Service")
            .setContentText("Stratux Foreground Service")
            .build()

        startForeground(101, notification)

        val wifiManager = getSystemService(WIFI_SERVICE) as WifiManager

        CoroutineScope(Dispatchers.IO).launch {

            TCPisRunning = true

            while (TCPisRunning) {
                if( wifiManager.isWifiEnabled() == true ){
                    AppData.connectionStatus = AppData.ConnectionStatus.NO_STRATUX
                    if(BuildConfig.DEBUG) Log.i(TAG, "TCP Service - Wifi enabled")
                    if(BuildConfig.DEBUG) Log.i(TAG, "Stratux TCP Receiver starting...")
                    AppData.stratuxTcpReceiver.start()
                    if(BuildConfig.DEBUG) Log.i(TAG, "Stratux TCP Receiver stopped!")
                    if( AppData.connectionAlarmIsSet == false ) {
                        if( AppData.vibration_alarm.value == 1 ) AppData.vibrator.vibrate(VibrationEffect.createWaveform(AppData.alarmConnectionPattern, AppData.alarmConnectionTiming, -1))
                        AppData.connectionAlarmIsSet = true    // will be set back to false in StratuxTcpReceiver when connection is successful
                    }
                } else {
                    AppData.connectionStatus = AppData.ConnectionStatus.NO_WIFI
                    if(BuildConfig.DEBUG) Log.i(TAG, "TCP Service - Wifi disabled")
                }
                Thread.sleep(3000)
            }
            if(BuildConfig.DEBUG) Log.i(TAG, "TCP Service is stopping")
            stopSelf()
        }

        CoroutineScope(Dispatchers.IO).launch {

            WSisRunning = true

            while (WSisRunning) {
                if(AppData.stratuxWsReceiver.isStratuxWsReceiverConnected() == false){
                    if( wifiManager.isWifiEnabled() == true ){
                        if(BuildConfig.DEBUG) Log.i(TAG, "WS Service - Wifi enabled")
                        if(BuildConfig.DEBUG) Log.i(TAG, "Stratux WS Receiver is starting")
                        AppData.stratuxWsReceiver.start()
                        if(AppData.stratuxWsReceiver.isStratuxWsReceiverConnected() == true) {
                            if(BuildConfig.DEBUG) Log.i(TAG, "Stratux WS Receiver started successfully")
                        } else {
                            if(BuildConfig.DEBUG) Log.e(TAG, "Stratux WS Receiver Start failed")
                        }
                    } else {
                        if(BuildConfig.DEBUG) Log.i(TAG, "WS Service - Wifi disabled")
                    }
                }
                Thread.sleep(3000)
            }
            if(BuildConfig.DEBUG) Log.i(TAG, "WS Service is stopping")
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TCPisRunning = false
        WSisRunning = false
        AppData.stratuxTcpReceiver.stop()
        AppData.stratuxWsReceiver.stop()
        if(BuildConfig.DEBUG) Log.i(TAG, "Service destroyed!")
    }

    private fun createNotificationChannel(context: Context):String{
        val channelID = "StratuxForegroundService"
        val channelName = "Stratux Foreground Service"

        val channel = NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.apply {
            lightColor = Color.BLUE
            importance = NotificationManager.IMPORTANCE_HIGH
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channelID
    }
}


