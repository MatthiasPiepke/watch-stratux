// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI

class StratuxStringData(var key: String, var value: String?)
class StratuxDoubleData(var key: String, var value: Double)
class StratuxBooleanData(var key: String, var value: Boolean)
class StratuxIntData(var key: String, var value: Int)

object StratuxStatusData {
    var version: StratuxStringData = StratuxStringData("Version", "")                           // string
    var cpuTemp: StratuxDoubleData = StratuxDoubleData("CPUTemp", 0.0)                           // double
    var gpsConnected: StratuxBooleanData = StratuxBooleanData("GPS_connected", false)                // bool
    var gpsSolution: StratuxStringData = StratuxStringData("GPS_solution", "")                  // string
    var gpsSatelliteTracked: StratuxIntData = StratuxIntData("GPS_satellites_locked", 0) // int
    var gpsSatelliteSeen: StratuxIntData = StratuxIntData("GPS_satellites_seen", 0) // int
    var bmpConnected: StratuxBooleanData = StratuxBooleanData("BMPConnected", false)                 // bool
    var imuConnected: StratuxBooleanData = StratuxBooleanData("IMUConnected", false)                 // bool
    var ognConnected: StratuxBooleanData = StratuxBooleanData("OGN_connected", false)                // bool
    var ognNoise: StratuxDoubleData = StratuxDoubleData("OGN_noise_db", 0.0)                     // double
    var ognGain: StratuxDoubleData = StratuxDoubleData("OGN_gain_db", 0.0)                       // double
    var esTrafficTracking: StratuxIntData = StratuxIntData("ES_traffic_targets_tracking", 0) // int
    var esMessagesLastMinute: StratuxIntData = StratuxIntData("ES_messages_last_minute", 0)  // int
    var esMessagesMax: StratuxIntData = StratuxIntData("ES_messages_max", 0)  // int
    var ognMessagesLastMinute: StratuxIntData = StratuxIntData("OGN_messages_last_minute", 0)  // int
    var ognMessagesMax: StratuxIntData = StratuxIntData("OGN_messages_max", 0)  // int
    var sdrDevices: StratuxIntData = StratuxIntData("Devices", 0)  // int
    var upTimeClock: StratuxStringData = StratuxStringData("UptimeClock", "")
    var newData = false
}

class StratuxWsReceiver() {

    private var webSocketClient = mWebSocketClient(URI("ws://"+AppData.preferences.ipAddress.value+"/status"))

    private val TAG = "StratuxWsReceiver"

    inner class mWebSocketClient(uri: URI) : WebSocketClient(uri) {

        override fun onOpen(handshakedata: ServerHandshake?) {
            if(BuildConfig.DEBUG) Log.i(TAG, "opened")
        }

        override fun onMessage(message: String?) {
            try {
                val jsonconversion = JSONObject(message!!)
                StratuxStatusData.version.value = jsonconversion.getString(StratuxStatusData.version.key)
                StratuxStatusData.cpuTemp.value = jsonconversion.getDouble(StratuxStatusData.cpuTemp.key)
                StratuxStatusData.gpsConnected.value = jsonconversion.getBoolean(StratuxStatusData.gpsConnected.key)
                StratuxStatusData.gpsSolution.value = jsonconversion.getString(StratuxStatusData.gpsSolution.key)
                StratuxStatusData.gpsSatelliteTracked.value = jsonconversion.getInt(StratuxStatusData.gpsSatelliteTracked.key)
                StratuxStatusData.gpsSatelliteSeen.value = jsonconversion.getInt(StratuxStatusData.gpsSatelliteSeen.key)
                //StratuxStatusData.bmpConnected.value = jsonconversion.getBoolean(StratuxStatusData.bmpConnected.key)
                //StratuxStatusData.imuConnected.value = jsonconversion.getBoolean(StratuxStatusData.imuConnected.key)
                StratuxStatusData.ognConnected.value = jsonconversion.getBoolean(StratuxStatusData.ognConnected.key)
                StratuxStatusData.ognNoise.value = jsonconversion.getDouble(StratuxStatusData.ognNoise.key)
                StratuxStatusData.ognGain.value = jsonconversion.getDouble(StratuxStatusData.ognGain.key)
                StratuxStatusData.esTrafficTracking.value = jsonconversion.getInt(StratuxStatusData.esTrafficTracking.key)
                StratuxStatusData.esMessagesLastMinute.value = jsonconversion.getInt(StratuxStatusData.esMessagesLastMinute.key)
                StratuxStatusData.esMessagesMax.value = jsonconversion.getInt(StratuxStatusData.esMessagesMax.key)
                StratuxStatusData.ognMessagesLastMinute.value = jsonconversion.getInt(StratuxStatusData.ognMessagesLastMinute.key)
                StratuxStatusData.ognMessagesMax.value = jsonconversion.getInt(StratuxStatusData.ognMessagesMax.key)
                StratuxStatusData.sdrDevices.value = jsonconversion.getInt(StratuxStatusData.sdrDevices.key)
                StratuxStatusData.upTimeClock.value = jsonconversion.getString(StratuxStatusData.upTimeClock.key)
                if(BuildConfig.DEBUG) Log.i(TAG, "Message")
                StratuxStatusData.newData = true
            } catch (e: Exception) {
                Log.e(TAG, "Error 6: $e")
            }
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            if(BuildConfig.DEBUG) Log.i(TAG, "closed")
        }

        override fun onError(ex: java.lang.Exception?) {
            if(BuildConfig.DEBUG) Log.e(TAG, "Error "+ex)
            webSocketClient.close()
        }
    }

    fun start() {
        webSocketClient = mWebSocketClient(URI("ws://"+AppData.preferences.ipAddress.value+"/status"))
        webSocketClient.connect()
        Thread.sleep(1000)
    }

    fun isStratuxWsReceiverConnected(): Boolean {
        return webSocketClient.isOpen
    }

    fun stop() {
        webSocketClient.close()
    }
}