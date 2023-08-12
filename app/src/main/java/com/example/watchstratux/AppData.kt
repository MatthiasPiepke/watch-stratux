// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.os.PowerManager
import android.os.Vibrator

object AppData {
    class Preferences {
        var ipAddress = AppPreference("IP_ADDRESS_KEY", BuildConfig.IP_ADDRESS)
        var ipPort = AppPreference("IP_PORT_KEY", 2000)

        var lowerVerticalLimit = AppPreference("LOWER_VERT_LIMIT_KEY", 0)
        var upperVerticalLimit = AppPreference("UPPER_VERT_LIMIT_KEY", 0)

        var showTracks = AppPreference("SHOW_TRACKS_KEY", true)
        var vibrationAlarm = AppPreference("ALARM_VIBRATION_KEY", true)
        var distanceUnitKm = AppPreference("DISTANCE_IN_KM_KEY", 1)      // 1 in KM, 0 in NM
        var altitudeUnitFt = AppPreference("ALTITUDE_IN_FT_KEY", 1)      // 1 in Feet, 0 in Meter

        var list = arrayOf(
            ipAddress,
            ipPort,
            lowerVerticalLimit,
            upperVerticalLimit,
            showTracks,
            vibrationAlarm,
            distanceUnitKm,
            altitudeUnitFt
        )
    }
    var preferences = Preferences()
    var defaultPreferences = Preferences()

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

    var displayWidth: Float = 396f
    var displayHeight: Float = 396f
    var displayCenterWidth: Float = displayWidth.div(2)
    var displayCenterHeight: Float = displayHeight.div(2)

    var zoomLevel = 0

    var connectionStatus = ConnectionStatus.NO_WIFI
    var connectionAlarmIsSet = false

    enum class ConnectionStatus {
        NO_WIFI, STRATUX_OK, NO_STRATUX, NO_GPS
    }

    lateinit var vibrator: Vibrator
    lateinit var powerManager: PowerManager
    lateinit var stratuxTcpReceiver: StratuxTcpReceiver
    lateinit var stratuxWsReceiver: StratuxWsReceiver
    lateinit var preferenceHandler: AppPreferenceHandler

    val alarmTrafficPattern = longArrayOf(500, 200, 500, 200, 500, 200, 500, 200, 500, 200)
    val alarmTrafficTiming = intArrayOf(255, 0, 255, 0, 255, 0, 255, 0, 255, 0)
    val alarmConnectionPattern = longArrayOf(500, 200, 500, 200)
    val alarmConnectionTiming = intArrayOf(128, 0, 128, 0)
    val alarmExitPattern = longArrayOf(600)
    val alarmExitTiming = intArrayOf(128)

    val aircraftMaxAge: Int = 15

    val compassItemText = arrayOf("N","I","I","E","I","I","S","I","I","W","I","I")
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
}