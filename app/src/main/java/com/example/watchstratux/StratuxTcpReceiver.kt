// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.os.VibrationEffect
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket
import java.util.regex.Pattern
import kotlin.Array
import kotlin.Exception
import kotlin.String

object StratuxData {

    object PFLAU {
        var rx = 0
        var tx = 0
        var gps = 0
        var power = 0
        var alarmLevel = 0
        var relativeBearing = 0
        var alarmType = ""
        var relativeVerticalMe = 0
        var relativeDistanceMe = 0
        var id = ""
    }

    object PFLAA {
        var alarmLevel = 0
        var relativeNorthMe = 0
        var relativeEastMe = 0
        var relativeVerticalMe = 0
        var idType = 0
        var id = ""
        var track = 0
        var turnRate = 0
        var groundSpeedMeSec = 0
        var climbRateMeSec = 0f
        var acftType = ""
    }

    object GPRMC {
        var utc = 0f
        var posStatus = ""
        var latitude = 0f
        var latitudeDirection = ""
        var longitude = 0f
        var longitudeDirection = ""
        var groundSpeedKn = 0f
        var track = 0f
    }

    object GPGGA {
        var altitudeMe = 0f
    }

    object GPGSA {
        var fix = 0
    }
}

class StratuxTcpReceiver {
    var isReceiverRunning = false
    var isReceiverLooping = false
    var alarmIsSet = false

    private val readlineTimeout: Timeout = Timeout(3000, "TCP Receiver -readline-")
    private val stopTimeout: Timeout = Timeout(3000, "TCP Receiver -stop-")

    private val TAG = "StratuxTcpReceiver"

    internal inner class ReadlineTimeoutEvent : Timeout.TimeoutEventListener {
        override fun OnTimeoutEvent() {
            if(BuildConfig.DEBUG) Log.e(TAG, "Timeout Event during readline")
            readlineTimeout.stop()
            stop()
        }
    }

    fun start() {
        val socket = Socket()

        if(isReceiverRunning == false) {
            try {
                socket.connect(InetSocketAddress(AppData.preferences.ipAddress.value, AppData.preferences.ipPort.value), 100)
            } catch (e: Exception) {
                if(BuildConfig.DEBUG) Log.e(TAG, "Error 2: Could not open the socket! $e")
            }

            if (socket.isConnected) {
                readlineTimeout.RegisterTimeoutEventListener(ReadlineTimeoutEvent())
                val socketReadStream = BufferedReader(InputStreamReader(socket.getInputStream()))
                readlineTimeout.start()
                isReceiverRunning = true
                isReceiverLooping = true
                AppData.connectionStatus = AppData.ConnectionStatus.NO_GPS
                AppData.connectionAlarmIsSet = false
                while (isReceiverLooping) {
                    if (socket.isConnected) {
                        var readline: String? = null
                        try {
                            readline = socketReadStream.readLine()
                        } catch (e: Exception) {
                            if(BuildConfig.DEBUG) Log.e(TAG, "Error 5: $e")
                        }
                        if (readline != null) {
                            readlineTimeout.reset()
                            processStratuxData(readline)
                            /*Log.i(TAG, "ID noPosition Distance  Speed  QDR  relBear  Alt  relAlt  Climb  Turnrate  Track  Age")
                            for (aircraft in AppData.aircraftList) {
                                Log.i(TAG, "" + aircraft.id + " " + aircraft.position + " " + aircraft.distanceMe + " " + aircraft.groundSpeedMeSec + " " + aircraft.qdr + " " + aircraft.relativeBearing + " " + aircraft.altitudeFt + " " + aircraft.relativeVerticalFt + " " + aircraft.climbRateFtSec + " " + aircraft.turnRate + " " + aircraft.track + " " + aircraft.ageSec)
                            }*/
                        }
                    } else {
                        if(BuildConfig.DEBUG) Log.e(TAG, "ERROR 3: Lost Connection")
                        isReceiverLooping = false
                    }
                }
                isReceiverRunning = false
                readlineTimeout.stop()
                try {
                    socket.close()
                } catch (e: Exception) {
                    if(BuildConfig.DEBUG) Log.e(TAG, "Error 4: Could not close the socket! $e")
                }
                AppData.connectionStatus = AppData.ConnectionStatus.NO_STRATUX
            }
        }
    }

    fun stop() {
        isReceiverLooping = false
        stopTimeout.start()
        while ( (isReceiverRunning == true) && (stopTimeout.isTimeout == false) )
        stopTimeout.stop()
    }

    private fun processStratuxData(msg: String) {
        // split received raw message into a usable string array
        val splitMsg: Array<String> = msg.split(",").toTypedArray()
        splitMsg[splitMsg.size-1] = splitMsg[splitMsg.size-1].split(Pattern.quote("*"))[0]

        // fill all empty fields with zeros, to avoid parse errors on runtime
        for (i in 0 until splitMsg.size) {
            if (splitMsg[i].equals("")) splitMsg[i] = "0"
        }

        if( splitMsg[0].equals("${'$'}PFLAU") == true ) {
            StratuxData.PFLAU.rx = splitMsg[1].toInt()
            StratuxData.PFLAU.tx = splitMsg[2].toInt()
            StratuxData.PFLAU.gps = splitMsg[3].toInt()
            StratuxData.PFLAU.power = splitMsg[4].toInt()
            StratuxData.PFLAU.alarmLevel = splitMsg[5].toInt()
            StratuxData.PFLAU.relativeBearing = splitMsg[6].toInt()
            StratuxData.PFLAU.alarmType = splitMsg[7]
            StratuxData.PFLAU.relativeVerticalMe = splitMsg[8].toInt()
            StratuxData.PFLAU.relativeDistanceMe = splitMsg[9].toInt()
            StratuxData.PFLAU.id = splitMsg[10]

            // remove aircraft with age older than max value (20 sec) and remove non ID aircraft after 1 sec
            val aircraft = AppData.aircraftList.iterator()
            while (aircraft.hasNext()) {
                val nextAircraft = aircraft.next()
                if (nextAircraft.id == "" && nextAircraft.ageSec >= 2) {
                    aircraft.remove()
                    Log.d(TAG, "aircraft removed, no ID")
                } else if (nextAircraft.ageSec >= AppData.aircraftMaxAge) {
                    aircraft.remove()
                    Log.d(TAG, "aircraft removed, ID: "+nextAircraft.id)
                }
                // increase age of each aircraft
                nextAircraft.ageSec++
            }

            // check for collision alarm, if ground speed > 54 kmh to avoid alarms during ground handling
            if( AppData.myAircraft.groundSpeedMeSec > 15) {
                if( AppData.preferences.vibrationAlarm.value == true ) {
                    if( StratuxData.PFLAU.alarmLevel != 0 ) {
                        if(alarmIsSet == false ){
                            AppData.vibrator.vibrate(VibrationEffect.createWaveform(AppData.alarmTrafficPattern, AppData.alarmTrafficTiming, -1))
                            alarmIsSet = true
                        }
                    } else {
                        alarmIsSet = false
                    }
                }
            }

        }

        if( splitMsg[0].equals("${'$'}PFLAA") == true ) {
            StratuxData.PFLAA.alarmLevel = splitMsg[1].toInt()
            StratuxData.PFLAA.relativeNorthMe = splitMsg[2].toInt()
            StratuxData.PFLAA.relativeEastMe = splitMsg[3].toInt()
            StratuxData.PFLAA.relativeVerticalMe = splitMsg[4].toInt()
            StratuxData.PFLAA.idType = splitMsg[5].toInt()
            StratuxData.PFLAA.id = splitMsg[6]
            StratuxData.PFLAA.track = splitMsg[7].toInt()
            StratuxData.PFLAA.turnRate = splitMsg[8].toInt()
            StratuxData.PFLAA.groundSpeedMeSec = splitMsg[9].toInt()
            StratuxData.PFLAA.climbRateMeSec = splitMsg[10].toFloat()
            StratuxData.PFLAA.acftType = splitMsg[11]

            var distanceMe = 0
            var position = false
            var qdr = 0.0f
            var relQdr = 0.0f
            var relBear = 0.0f

            // check if GPS fix has 3D
            if( AppData.connectionStatus == AppData.ConnectionStatus.STRATUX_OK ) {
                // analyse PFLAA and update AppData AircraftList

                // calculate distance
                if( StratuxData.PFLAA.relativeEastMe == 0 ){
                    position = false
                    distanceMe = StratuxData.PFLAA.relativeNorthMe
                } else {
                    position = true
                    distanceMe = Math.sqrt(
                        Math.pow(
                            StratuxData.PFLAA.relativeNorthMe.toDouble(),
                            2.0
                        ) + Math.pow(StratuxData.PFLAA.relativeEastMe.toDouble(), 2.0)
                    ).toInt()

                    // calculate qdr (position relative to north)
                    qdr = Math.asin( (Math.abs(StratuxData.PFLAA.relativeEastMe).toFloat() / distanceMe.toFloat()).toDouble()).toFloat() * 180f / Math.PI.toFloat()
                    if (StratuxData.PFLAA.relativeNorthMe < 0 && StratuxData.PFLAA.relativeEastMe > 0) qdr = 180 - qdr
                    if (StratuxData.PFLAA.relativeNorthMe > 0 && StratuxData.PFLAA.relativeEastMe < 0) qdr = 360 - qdr
                    if (StratuxData.PFLAA.relativeNorthMe < 0 && StratuxData.PFLAA.relativeEastMe < 0) qdr = 180 + qdr

                    // calculate relative qdr (position relative to me)
                    relQdr = qdr - AppData.myAircraft.track
                    // calculate relative bearing (same as relQdr but -180 to 180)
                    relBear = relQdr
                    if (relQdr < -180) relBear += 360 else if (relQdr > 180) relBear -= 360
                }

                // relative Vertical distance and altitude
                var relVertFt = (StratuxData.PFLAA.relativeVerticalMe * 3.281).toInt()
                var altFt = (AppData.myAircraft.altitudeFt + relVertFt).toInt()

                // update aircraft list
                var aircraftIdExists = false
                for (aircraft in AppData.aircraftList) {
                    // do not update aircraft without ID
                    if( aircraft.id != "" ){
                        // update existing aircrafts
                        if(aircraft.id.equals(StratuxData.PFLAA.id) == true) {
                            // update data on existing aircraft in list
                            aircraft.alarmLevel = StratuxData.PFLAA.alarmLevel
                            aircraft.position = position
                            aircraft.distanceMe = distanceMe
                            aircraft.groundSpeedMeSec = StratuxData.PFLAA.groundSpeedMeSec
                            aircraft.qdr = qdr
                            aircraft.relativeBearing = relBear
                            aircraft.altitudeFt = altFt
                            aircraft.relativeVerticalFt = relVertFt
                            aircraft.climbRateFtSec = StratuxData.PFLAA.climbRateMeSec*3.281f
                            aircraft.turnRate = StratuxData.PFLAA.turnRate
                            aircraft.track = StratuxData.PFLAA.track
                            aircraft.ageSec = 0

                            aircraftIdExists = true
                        }
                    }
                }
                // add unknown aircraft or aircraft without ID
                if (aircraftIdExists == false) {
                    AppData.aircraftList.add(AppData.Aircraft(
                        StratuxData.PFLAA.id,
                        StratuxData.PFLAA.alarmLevel,
                        position,
                        distanceMe,
                        StratuxData.PFLAA.groundSpeedMeSec,
                        qdr,
                        relBear,
                        altFt,
                        relVertFt,
                        StratuxData.PFLAA.climbRateMeSec,
                        StratuxData.PFLAA.turnRate,
                        StratuxData.PFLAA.track,
                        0
                    ))
                }
            }
        }

        if( splitMsg[0].equals("${'$'}GPRMC") == true ) {
            //StratuxData.GPRMC.utc = splitMsg[1].toFloat()
            //StratuxData.GPRMC.posStatus = splitMsg[2]
            //StratuxData.GPRMC.latitude = splitMsg[3].toFloat()
            //StratuxData.GPRMC.latitudeDirection = splitMsg[4]
            //StratuxData.GPRMC.longitude = splitMsg[5].toFloat()
            //StratuxData.GPRMC.longitudeDirection = splitMsg[6]
            StratuxData.GPRMC.groundSpeedKn = splitMsg[7].toFloat()
            StratuxData.GPRMC.track = splitMsg[8].toFloat()
            AppData.myAircraft.groundSpeedMeSec = StratuxData.GPRMC.groundSpeedKn*0.514444f
            AppData.myAircraft.track = StratuxData.GPRMC.track.toInt()
        }

        if( splitMsg[0].equals("${'$'}GPGGA") == true ) {
            StratuxData.GPGGA.altitudeMe = splitMsg[9].toFloat()
            AppData.myAircraft.altitudeFt = StratuxData.GPGGA.altitudeMe * 3.281f
        }

        if( splitMsg[0].equals("${'$'}GPGSA") == true ) {
            StratuxData.GPGSA.fix = splitMsg[2].toInt()
            if( StratuxData.GPGSA.fix == 3 ) AppData.connectionStatus = AppData.ConnectionStatus.STRATUX_OK
            else AppData.connectionStatus = AppData.ConnectionStatus.NO_GPS
        }
    }
}