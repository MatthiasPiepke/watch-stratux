// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


class MainRecyclerViewAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var updateRadarView = false
    var updateSystemInfo = false

    var handlerRadarView = Handler(Looper.getMainLooper())
    lateinit var runnableRadarView: Runnable
    var handlerSystemInfo = Handler(Looper.getMainLooper())
    lateinit var runnableSystemInfo: Runnable

    private val TAG = "MainView"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item_view_1: View = LayoutInflater.from(parent.context).inflate(R.layout.main_recycler_item_view_1, parent, false)
        val item_view_2: View = LayoutInflater.from(parent.context).inflate(R.layout.main_recycler_item_view_2, parent, false)
        val item_view_3: View = LayoutInflater.from(parent.context).inflate(R.layout.main_recycler_item_view_3, parent, false)

        val wearRecyclerViewHolder1 = WearRecyclerViewHolder1(item_view_1)
        val wearRecyclerViewHolder2 = WearRecyclerViewHolder2(item_view_2)
        val wearRecyclerViewHolder3 = WearRecyclerViewHolder3(item_view_3)

        when (viewType) {
            0 -> return wearRecyclerViewHolder1
            1 -> return wearRecyclerViewHolder2
            2 -> return wearRecyclerViewHolder3
        }
        return WearRecyclerViewHolder1(item_view_1)
    }

    inner class WearRecyclerViewHolder1(view: View) : ViewHolder(view) {
        init {
            val relativeLayout: RelativeLayout = view.findViewById(R.id.main_recycler_item_1_relative_view)
            val radarPaintView = RadarPaintView(relativeLayout.context)
            relativeLayout.addView(radarPaintView)
            val zoomInButton: Button = view.findViewById(R.id.main_recycler_item_1_button_zoom_in)
            val zoomOutButton: Button = view.findViewById(R.id.main_recycler_item_1_button_zoom_out)
            zoomInButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    // Code here executes on main thread after user pressed button
                    if (AppData.zoomLevel < AppData.numberOfZoomLevels - 1) {
                        AppData.zoomLevel++
                        radarPaintView.invalidate()
                        AppData.vibrator.vibrate(VibrationEffect.createOneShot(50, 50))
                    }
                }
            })
            zoomOutButton.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    // Code here executes on main thread after user pressed button
                    if (AppData.zoomLevel > 0) {
                        AppData.zoomLevel--
                        radarPaintView.invalidate()
                        AppData.vibrator.vibrate(VibrationEffect.createOneShot(50, 50))
                    }
                }
            })
            updateRadarView = true

            runnableRadarView = object : Runnable {
                override fun run() {
                    if (updateRadarView) {
                        if(BuildConfig.DEBUG) Log.i(TAG, "update RadarView")
                        radarPaintView.invalidate()
                    }
                    handlerRadarView.postDelayed(this, 1000)
                }
            }
            handlerRadarView.post(runnableRadarView)
        }
    }

    inner class WearRecyclerViewHolder2(view: View) : ViewHolder(view) {
        init {
            val textView1 = view.findViewById(R.id.text1) as TextView
            val textView2 = view.findViewById(R.id.text2) as TextView
            val textView3 = view.findViewById(R.id.text3) as TextView
            val textView4 = view.findViewById(R.id.text4) as TextView
            val textView5 = view.findViewById(R.id.text5) as TextView
            val textView6 = view.findViewById(R.id.text6) as TextView
            val textView7 = view.findViewById(R.id.text7) as TextView
            val textView8 = view.findViewById(R.id.text8) as TextView
            val textView9 = view.findViewById(R.id.text9) as TextView

            updateSystemInfo = true

            runnableSystemInfo = object : Runnable {
                override fun run() {
                    if(updateSystemInfo) {
                        if (StratuxStatusData.newData) {
                            if(BuildConfig.DEBUG) Log.i(TAG, "update SystemInfo")
                            textView1.setText(StratuxStatusData.version.value)
                            textView2.setText(String.format("%.1f °C", StratuxStatusData.cpuTemp.value))

                            if (StratuxStatusData.gpsConnected.value) {
                                textView3.setText(StratuxStatusData.gpsSolution.value)
                                textView4.setText("" + StratuxStatusData.gpsSatelliteSeen.value + " seen, " + StratuxStatusData.gpsSatelliteTracked.value + " tracked")
                            } else {
                                textView3.setText("Disconnected")
                                textView4.setText("Disconnected")
                            }

                            textView7.setText("" + StratuxStatusData.esMessagesLastMinute.value + " current")
                            textView8.setText("" + StratuxStatusData.esTrafficTracking.value)

                            if (StratuxStatusData.ognConnected.value) {
                                textView5.setText("" + StratuxStatusData.ognMessagesLastMinute.value)
                                textView6.setText("" + StratuxStatusData.ognNoise.value + "@" + StratuxStatusData.ognGain.value)
                            } else {
                                textView5.setText("Disconnected")
                                textView6.setText("Disconnected")
                            }
                            textView9.setText(
                                StratuxStatusData.upTimeClock.value?.substring(
                                    StratuxStatusData.upTimeClock.value!!.indexOf('T') + 1,
                                    StratuxStatusData.upTimeClock.value!!.indexOf('T') + 9
                                )
                            )

                            StratuxStatusData.newData = false
                        }
                    }
                    handlerSystemInfo.postDelayed(this, 1000)
                }
            }
            handlerSystemInfo.post(runnableSystemInfo)
        }
    }

    inner class WearRecyclerViewHolder3(view: View) : ViewHolder(view) {
        init {
            val cardView1: CardView = view.findViewById(R.id.main_recycler_item_3_card_view_1)
            cardView1.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    context.startActivity(Intent(context.getApplicationContext(), SettingsActivity::class.java))
                }
            })
            val cardView2: CardView = view.findViewById(R.id.main_recycler_item_3_card_view_2)
            cardView2.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    (context as Activity).finish()
                }
            })
        }
    }


    override fun getItemCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}