// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.BatteryManager
import android.view.View
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

data class AircraftIcon(val body: Path, val track: Path, val textX: Int, val textY: Int, val textLeftAligned: Boolean)

fun createAircraftIcon(distance: Int, relBearing: Float, relTrack: Int, groundSpeedMeSec: Int): AircraftIcon {

    // calculate x,y offsets
    val distance_in_px = (distance.toFloat() * AppData.displayWidth / (2 * AppData.zoomLevelRange[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel]).toFloat()).toInt()
    val x = (AppData.displayWidth / 2 + distance_in_px * Math.sin(relBearing / 180.0 * Math.PI)).toInt()
    val y = (AppData.displayHeight / 2 - distance_in_px * Math.cos(relBearing / 180.0 * Math.PI)).toInt()
    val ax = -12
    val ay = -24
    val bx = 0
    val by = -18
    val cx = 12
    val cy = -24
    val sin_a = Math.sin(relTrack / 180.0 * Math.PI).toFloat()
    val cos_a = Math.cos(relTrack / 180.0 * Math.PI).toFloat()
    val ax_new = (cos_a * ax + sin_a * ay).toInt()
    val ay_new = (cos_a * ay - sin_a * ax).toInt()
    val bx_new = (cos_a * bx + sin_a * by).toInt()
    val by_new = (cos_a * by - sin_a * bx).toInt()
    val cx_new = (cos_a * cx + sin_a * cy).toInt()
    val cy_new = (cos_a * cy - sin_a * cx).toInt()
    val bodyPath = Path()
    bodyPath.moveTo(x.toFloat(), y.toFloat()) // Top
    bodyPath.lineTo((x - ax_new).toFloat(), (y - ay_new).toFloat()) // Left
    bodyPath.lineTo((x - bx_new).toFloat(), (y - by_new).toFloat()) // Bottom
    bodyPath.lineTo((x - cx_new).toFloat(), (y - cy_new).toFloat()) // Right
    bodyPath.lineTo(x.toFloat(), y.toFloat()) // Back to Top
    bodyPath.close()

    val groundSpeedMeMin = groundSpeedMeSec * 60
    val groundSpeed_in_px = (groundSpeedMeMin * AppData.displayWidth / (2 * AppData.zoomLevelRange[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel])).toInt()
    val u = (x - groundSpeed_in_px * Math.sin(relTrack / 180.0 * Math.PI)).toInt()
    val v = (y - groundSpeed_in_px * Math.cos(relTrack / 180.0 * Math.PI)).toInt()
    val trackPath = Path()
    trackPath.moveTo(x.toFloat(), y.toFloat())
    trackPath.lineTo(u.toFloat(), v.toFloat())
    trackPath.close()

    var tx = ax
    if (relTrack < -90) tx = cx
    if (relTrack > 90) tx = cx
    val ty = 0
    var tx_new = x - (cos_a * tx + sin_a * ty).toInt()
    var ty_new = y - (cos_a * ty - sin_a * tx).toInt()

    var textLeftAligned: Boolean = false
    // 0 - 90
    if( relTrack < 0 ) {
        if (relTrack >= -90) {
            textLeftAligned = true
        }
        // 90 - 180
        else if (relTrack < -90) {
            textLeftAligned = true
            ty_new -= 18
        }
    } else {
        // 180 - 270
        if (relTrack <= 90) {
            textLeftAligned = true
            ty_new -= 18
        }
        // 270 - 360
        else if (relTrack > 90) {
            textLeftAligned = true
        }
    }



    return AircraftIcon(bodyPath, trackPath, tx_new, ty_new, textLeftAligned)
}

class RadarPaintView(context: Context) : View(context) {
    val radarInnerCircleRadius = AppData.displayWidth/6
    val radarOuterCircleRadius = radarInnerCircleRadius * 2
    val radarPaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // set background black
        radarPaint.style = Paint.Style.FILL
        radarPaint.color = Color.BLACK
        canvas.drawPaint(radarPaint)

        // draw radar circles
        radarPaint.flags = Paint.ANTI_ALIAS_FLAG
        radarPaint.color = ContextCompat.getColor(context, R.color.radar_circles)
        radarPaint.style = Paint.Style.STROKE
        canvas.drawArc(
            (AppData.displayCenterWidth - radarInnerCircleRadius),
            (AppData.displayCenterHeight - radarInnerCircleRadius),
            (AppData.displayCenterWidth + radarInnerCircleRadius),
            (AppData.displayCenterHeight + radarInnerCircleRadius),
            0f,
            360f,
            false,
            radarPaint
        )
        canvas.drawArc(
            (AppData.displayCenterWidth - radarOuterCircleRadius),
            (AppData.displayCenterHeight - radarOuterCircleRadius),
            (AppData.displayCenterWidth + radarOuterCircleRadius),
            (AppData.displayCenterHeight + radarOuterCircleRadius),
            0f,
            360f,
            false,
            radarPaint
        )

        // draw text on radar circles
        val typeface = resources.getFont(R.font.radar_number_font)
        radarPaint.color = ContextCompat.getColor(context, R.color.radar_numbers)
        radarPaint.style = Paint.Style.FILL
        radarPaint.typeface = typeface
        radarPaint.textSize = 18f
        radarPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText(
            AppData.radarInnerCircleZoomLevel[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel],
            (AppData.displayCenterWidth - radarInnerCircleRadius - 5),
            (AppData.displayCenterHeight + 7),
            radarPaint
        )
        canvas.drawText(
            AppData.radarOuterCircleZoomLevel[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel],
            (AppData.displayCenterWidth - radarOuterCircleRadius - 5),
            (AppData.displayCenterHeight + 7),
            radarPaint
        )
        radarPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(
            AppData.radarInnerCircleZoomLevel[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel],
            (AppData.displayCenterWidth + radarInnerCircleRadius + 5),
            (AppData.displayCenterHeight + 7),
            radarPaint
        )
        canvas.drawText(
            AppData.radarOuterCircleZoomLevel[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel],
            (AppData.displayCenterWidth + radarOuterCircleRadius + 5),
            (AppData.displayCenterHeight + 7),
            radarPaint
        )

        // draw all other things below here only if stratux is connected and stratux gps is 3d fix
        if ( AppData.connectionStatus == AppData.ConnectionStatus.STRATUX_OK ) {
            // draw compass
            canvas.save()
            canvas.rotate(
                (AppData.myAircraft.track * -1).toFloat(),
                (AppData.displayWidth / 2), (AppData.displayWidth / 2)
            )
            for (compassText in AppData.compassItemText) {
                val x = (AppData.displayWidth / 2 - radarPaint.measureText(compassText) / 2)
                val y = 16f
                canvas.drawText(compassText, x, y, radarPaint)
                canvas.rotate(
                    30f,
                    (AppData.displayWidth / 2),
                    (AppData.displayWidth / 2)
                )
            }
            canvas.restore()

            // draw heading text
            val str = "" + AppData.myAircraft.track
            radarPaint.color = Color.BLACK
            canvas.drawRect(
                AppData.displayWidth / 2 - radarPaint.measureText(str) / 2 - 5,
                0f,
                AppData.displayWidth / 2 + radarPaint.measureText(str) / 2 + 5,
                (16 + 2).toFloat(),
                radarPaint
            )
            radarPaint.color = ContextCompat.getColor(context, R.color.radar_numbers)
            canvas.drawText(
                str,
                (AppData.displayWidth / 2 - radarPaint.measureText(str) / 2),
                16f,
                radarPaint
            )

            // draw other aircrafts
            if( AppData.aircraftList.isEmpty() == false){
                for(aircraft in AppData.aircraftList){
                    // draw only aircrafts within zoom range
                    if( aircraft.distanceMe < AppData.zoomLevelRange[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel] ){
                        // draw only aircrafts within the given vertical view settings
                        var aircraftIsTooLow = false
                        var aircraftIsTooHigh = false
                        if (AppData.preferences.lowerVerticalLimit.value != 0) {
                            if (aircraft.relativeVerticalFt < AppData.lowerLimitValues[AppData.preferences.lowerVerticalLimit.value].toInt()) aircraftIsTooLow = true
                        }
                        if (AppData.preferences.upperVerticalLimit.value != 0) {
                            if (aircraft.relativeVerticalFt > AppData.upperLimitValues[AppData.preferences.upperVerticalLimit.value].toInt()) aircraftIsTooHigh = true
                        }
                        if( (aircraftIsTooLow == false) && (aircraftIsTooHigh == false) ){
                            // draw aircraft icon if position is known, if not known draw circle
                            if( aircraft.position == true ){
                                var relTrack = (AppData.myAircraft.track - aircraft.track).toInt()
                                if (relTrack < -180) relTrack += 360
                                if (relTrack > 180) relTrack -= 360

                                var aircraftIcon: AircraftIcon = createAircraftIcon( aircraft.distanceMe, aircraft.relativeBearing, relTrack, aircraft.groundSpeedMeSec )

                                // prepare rel Vert Text
                                if( aircraftIcon.textLeftAligned ) radarPaint.textAlign = Paint.Align.LEFT else radarPaint.textAlign = Paint.Align.RIGHT
                                var relVertText = if (aircraft.relativeVerticalFt >= 0) "+" else "-"
                                if( AppData.preferences.altitudeUnitFt.value == 1 ) relVertText += (aircraft.relativeVerticalFt / 100)
                                else relVertText += "%.1f".format(aircraft.relativeVerticalFt/3.281f / 100f)
                                if (aircraft.climbRateFtSec > 0) relVertText += "▲" else if (aircraft.climbRateFtSec < 0) relVertText += "▼"

                                if( aircraft.ageSec <= 5 ){
                                    // draw draw body
                                    if (aircraft.alarmLevel == 0) radarPaint.color = Color.GREEN else radarPaint.color = Color.RED
                                    radarPaint.style = Paint.Style.FILL_AND_STROKE
                                    canvas.drawPath(aircraftIcon.body, radarPaint)
                                    radarPaint.color = Color.WHITE
                                    radarPaint.style = Paint.Style.STROKE
                                    //canvas.drawLine(aircraftIcon.x.toFloat(), aircraftIcon.y.toFloat(), aircraftIcon.textX.toFloat(), aircraftIcon.textY.toFloat(), radarPaint)
                                    if( AppData.preferences.showTracks.value as Boolean == true ) canvas.drawPath(aircraftIcon.track, radarPaint)
                                    // draw relVert text
                                    radarPaint.style = Paint.Style.FILL
                                    canvas.drawText(relVertText, aircraftIcon.textX.toFloat(), (aircraftIcon.textY + 18).toFloat(), radarPaint)
                                } else {
                                    // draw draw body
                                    radarPaint.color = Color.GRAY
                                    radarPaint.style = Paint.Style.FILL
                                    canvas.drawPath(aircraftIcon.body, radarPaint)
                                    radarPaint.style = Paint.Style.STROKE
                                    if( AppData.preferences.showTracks.value as Boolean == true ) canvas.drawPath(aircraftIcon.track, radarPaint)
                                    // draw relVert text
                                    radarPaint.style = Paint.Style.FILL
                                    canvas.drawText(relVertText, aircraftIcon.textX.toFloat(), (aircraftIcon.textY + 18).toFloat(), radarPaint)
                                }
                            } else {
                                // draw circle if position in unknown
                                if (aircraft.ageSec <= 10) {
                                    if (aircraft.alarmLevel == 0) radarPaint.color = Color.GREEN else radarPaint.color = Color.RED
                                } else {
                                    radarPaint.color = Color.GRAY
                                }
                                radarPaint.style = Paint.Style.STROKE
                                var distance_in_px = aircraft.distanceMe*AppData.displayWidth/(2*AppData.zoomLevelRange[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel])
                                canvas.drawArc(
                                    AppData.displayCenterWidth - distance_in_px,
                                    AppData.displayCenterHeight - distance_in_px,
                                    AppData.displayCenterWidth + distance_in_px,
                                    AppData.displayCenterHeight + distance_in_px,
                                    0f,
                                    360f,
                                    false,
                                    radarPaint
                                )
                                // draw relVert text
                                radarPaint.style = Paint.Style.FILL
                                radarPaint.textAlign = Paint.Align.LEFT
                                var relVertText = if (aircraft.relativeVerticalFt >= 0) "+" else "-"
                                if( AppData.preferences.altitudeUnitFt.value == 1 ) relVertText += (aircraft.relativeVerticalFt / 100)
                                else relVertText += "%.1f".format(aircraft.relativeVerticalFt/3.281f / 100f)
                                if (aircraft.climbRateFtSec > 0) relVertText += "▲" else if (aircraft.climbRateFtSec < 0) relVertText += "▼"
                                canvas.drawText(relVertText, AppData.displayCenterWidth - radarPaint.measureText(relVertText) / 2, AppData.displayCenterHeight - distance_in_px - 3, radarPaint)
                            }
                        }
                    }
                }
            }

            // draw aircraft white icon
            val bMap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_icon_80_white), 40, 40, false)
            canvas.drawBitmap(
                bMap,
                AppData.displayCenterWidth - 20,
                AppData.displayCenterHeight - 20,
                radarPaint
            )
            // draw my track
            if( AppData.preferences.showTracks.value as Boolean == true ) {
                val myGroundSpeed_in_px = (AppData.MyAircraft.groundSpeedMeSec * 60 * AppData.displayWidth / (2 * AppData.zoomLevelRange[AppData.preferences.distanceUnitKm.value][AppData.zoomLevel])).toInt()
                radarPaint.color = Color.WHITE
                radarPaint.flags = 0
                canvas.drawLine(AppData.displayCenterWidth, AppData.displayCenterHeight, AppData.displayCenterWidth, AppData.displayCenterHeight - myGroundSpeed_in_px, radarPaint)
                radarPaint.flags = Paint.ANTI_ALIAS_FLAG
            }
        } else {
            // here something went wrong with stratux connection or data
            // draw aircraft red icon
            val bMap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plane_icon_80_red), 40, 40, false)
            canvas.drawBitmap(
                bMap,
                AppData.displayCenterWidth - 20,
                AppData.displayCenterHeight - 20,
                radarPaint
            )

            // draw error message
            radarPaint.typeface = resources.getFont(R.font.menu_font)
            radarPaint.textSize = 30f
            radarPaint.color = Color.RED
            radarPaint.style = Paint.Style.FILL

            // show right error message
            var noConnectText1 = ""
            var noConnectText2 = ""
            when (AppData.connectionStatus) {
                AppData.ConnectionStatus.NO_STRATUX -> {
                    noConnectText1 = "NO STRATUX on"
                    noConnectText2 = AppData.preferences.ipAddress.value as String + ":" + AppData.preferences.ipPort.value.toString()
                }
                AppData.ConnectionStatus.NO_WIFI -> {
                    noConnectText1 = "WIFI"
                    noConnectText2 = "NOT CONNECTED"
                }
                AppData.ConnectionStatus.NO_GPS -> {
                    noConnectText1 = "STRATUX"
                    noConnectText2 = "HAS NO GPS"
                }
                else -> {
                    noConnectText1 = "UNKNOWN"
                    noConnectText2 = "ERROR"
                }
            }
            canvas.drawText(
                noConnectText1,
                (AppData.displayWidth / 2 - radarPaint.measureText(noConnectText1) / 2),
                120f,
                radarPaint
            )
            canvas.drawText(
                noConnectText2,
                (AppData.displayWidth / 2 - radarPaint.measureText(noConnectText2) / 2),
                160f,
                radarPaint
            )
        }

        // draw battery level
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val batteryPct: Int? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale
        }
        var bMap: Bitmap
        if( batteryPct!! < 25 ){
            bMap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_red), 60, 25, false)
        } else {
            bMap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.battery_grey), 60, 25, false)
        }
        canvas.drawBitmap(
            bMap,
            AppData.displayCenterWidth - 30,
            AppData.displayHeight - 84,
            radarPaint
        )
        radarPaint.typeface = resources.getFont(R.font.clock_font)
        radarPaint.textSize = 18f
        radarPaint.textAlign = Paint.Align.CENTER
        radarPaint.color = ContextCompat.getColor(context, R.color.radar_numbers)
        canvas.drawText(
            batteryPct.toString()+"%",
            AppData.displayCenterHeight,
            AppData.displayHeight - 66,
            radarPaint
        )

        // draw time
        val date = Date()
        val formatter = SimpleDateFormat("HH:mm")
        val text = formatter.format(date)
        radarPaint.typeface = resources.getFont(R.font.clock_font)
        radarPaint.textSize = 44f
        radarPaint.textAlign = Paint.Align.CENTER
        radarPaint.color = Color.BLACK
        radarPaint.style = Paint.Style.FILL
        canvas.drawRect(
            AppData.displayCenterWidth - radarPaint.measureText(text) / 2 - 5,
            AppData.displayHeight - 40,
            AppData.displayCenterHeight + radarPaint.measureText(text) / 2 + 5,
            AppData.displayHeight,
            radarPaint
        )
        radarPaint.color = Color.WHITE
        canvas.drawText(
            text,
            AppData.displayCenterHeight,
            AppData.displayHeight - 10,
            radarPaint
        )
    }
}