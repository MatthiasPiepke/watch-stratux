// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Application
import android.os.PowerManager
import android.os.Vibrator

object AppData {
    var ip_1: AppPreference = AppPreference(DefaultPreferences.ip_1.key, DefaultPreferences.ip_1.value)
    var ip_2: AppPreference = AppPreference(DefaultPreferences.ip_2.key, DefaultPreferences.ip_2.value)
    var ip_3: AppPreference = AppPreference(DefaultPreferences.ip_3.key, DefaultPreferences.ip_3.value)
    var ip_4: AppPreference = AppPreference(DefaultPreferences.ip_4.key, DefaultPreferences.ip_4.value)
    var ip_port: AppPreference = AppPreference(DefaultPreferences.ip_port.key, DefaultPreferences.ip_port.value)

    var lower_vertical_limit: AppPreference = AppPreference(DefaultPreferences.lower_vertical_limit.key, DefaultPreferences.lower_vertical_limit.value)
    var upper_vertical_limit: AppPreference = AppPreference(DefaultPreferences.upper_vertical_limit.key, DefaultPreferences.upper_vertical_limit.value)

    var show_tracks: AppPreference = AppPreference(DefaultPreferences.show_tracks.key, DefaultPreferences.show_tracks.value)
    var vibration_alarm: AppPreference = AppPreference(DefaultPreferences.vibration_alarm.key, DefaultPreferences.vibration_alarm.value)
    var distance_in_km: AppPreference = AppPreference(DefaultPreferences.distance_in_km.key, DefaultPreferences.distance_in_km.value)
    var altitude_in_ft: AppPreference = AppPreference(DefaultPreferences.altitude_in_ft.key, DefaultPreferences.altitude_in_ft.value)

    var preferences: Array<AppPreference> = arrayOf<AppPreference>(
        ip_1,
        ip_2,
        ip_3,
        ip_4,
        ip_port,
        lower_vertical_limit,
        upper_vertical_limit,
        show_tracks,
        vibration_alarm,
        distance_in_km,
        altitude_in_ft
    )

    lateinit var preferenceHandler: AppPreferenceHandler

    object MyAircraft {
        var groundSpeedMeSec = 0.0f
        var altitudeFt = 0.0f
        var track = 0
    }

    var myAircraft = MyAircraft

    class Aircraft(
        var id: String,
        var alarmLevel: Int,
        var position: Boolean,
        var distanceMe: Int,
        var groundSpeedMeSec: Int,
        var qdr: Float,
        var relativeBearing: Float,
        var altitudeFt: Int,
        var relativeVerticalFt: Int,
        var climbRateFtSec: Float,
        var turnRate: Int,
        var track: Int,
        var ageSec: Int
    )

    var aircraftList: MutableList<Aircraft> = mutableListOf<Aircraft>()

    var aircraftMaxAge: Int = 20
    var gpsFix: Boolean = false

    var displayWidth: Float = 396f
    var displayHeight: Float = 396f
    var displayCenterWidth: Float = displayWidth.div(2)
    var displayCenterHeight: Float = displayHeight.div(2)

    val alarmTrafficPattern = longArrayOf(500, 200, 500, 200, 500, 200, 500, 200, 500, 200)
    val alarmTrafficTiming = intArrayOf(255, 0, 255, 0, 255, 0, 255, 0, 255, 0)

    var isAppAcitve = false
    lateinit var vibrator: Vibrator
    lateinit var powerManager: PowerManager

    var connectionStatus = ConnectionStatus.NO_WIFI
    val compassItemText = arrayOf("N","I","I","E","I","I","S","I","I","W","I","I")
    var zoomLevel = 0
    val zoomLevelRange = arrayOf(intArrayOf(44448, 29632, 14816, 11112, 5556, 3704, 1852), intArrayOf(45000, 30000, 15000, 9000, 6000, 3000, 1500))
    val numberOfZoomLevels: Int = zoomLevelRange[0].size
    val radarInnerCircleZoomLevel = arrayOf(arrayOf("8", "6", "3", "2", "1", "0.5", "0.25"), arrayOf("15", "10", "5", "3", "2", "1", "0.5"))
    val radarOuterCircleZoomLevel = arrayOf(arrayOf("16", "8", "6", "3", "2", "1", "0.5"), arrayOf("30", "20", "10", "6", "4", "2", "1"))
    val lowerLimitValues = arrayOf(
        "-∞",
        "-10000",
        "-7500",
        "-5000",
        "-3000",
        "-2500",
        "-2000",
        "-1500",
        "-1000",
        "-500",
        "0"
    )
    val upperLimitValues = arrayOf(
        "∞",
        "10000",
        "7500",
        "5000",
        "3000",
        "2500",
        "2000",
        "1500",
        "1000",
        "500",
        "0"
    )

    lateinit var stratuxTcpReceiver: StratuxTcpReceiver
    lateinit var stratuxWsReceiver: StratuxWsReceiver

    enum class ConnectionStatus {
        NO_WIFI, STRATUX, NO_STRATUX
    }
}