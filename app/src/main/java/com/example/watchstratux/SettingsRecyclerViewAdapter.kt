// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


class SettingsRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<SettingsRecyclerViewAdapter.RecyclerViewHolder>(){
    var context: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.settings_menu_button_item, parent, false)
        return RecyclerViewHolder(view)
    }

    class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewItem_up: TextView = view.findViewById(R.id.settings_menu_textview_up)
        var textViewItem_down: TextView = view.findViewById(R.id.settings_menu_textview_down)
        var switch: Switch = view.findViewById(R.id.settings_menu_switch)
        var cardView: CardView = view.findViewById(R.id.setting_menu_cardview)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        if (position == 0){
            holder.switch.visibility = View.GONE
            holder.textViewItem_up.setText("IP Settings")
            holder.textViewItem_down.setText("" + AppData.ip_1.value.toString() + "." + AppData.ip_2.value.toString() + "." + AppData.ip_3.value.toString() + "." + AppData.ip_4.value.toString() + " : " + AppData.ip_port.value.toString())
            holder.cardView.setOnClickListener {
                context.startActivity(Intent(context, IpAddressActivity::class.java))
            }
        }
        if (position == 1){
            holder.switch.visibility = View.GONE
            holder.textViewItem_up.textSize = 14f
            holder.textViewItem_up.setText("Vertical\nTraffic Visibility")
            holder.textViewItem_down.visibility = View.GONE
            holder.cardView.setOnClickListener {
                context.startActivity(Intent(context, VerticalLimitActivity::class.java))
            }
        }
        if (position == 2){
            holder.textViewItem_up.textSize = 14f
            holder.textViewItem_up.setText("Vibration Alarm")
            holder.textViewItem_down.visibility = View.GONE
            holder.switch.setChecked(if( AppData.vibration_alarm.value == 1 ) true else false)
            holder.switch.setOnClickListener {
                if( holder.switch.isChecked == true ) AppData.vibration_alarm.value = 1
                else AppData.vibration_alarm.value = 0
                AppData.preferenceHandler.savePreference(AppData.vibration_alarm)
            }
            holder.cardView.setOnClickListener {
                if( holder.switch.isChecked == true ) {
                    holder.switch.setChecked(false)
                    AppData.vibration_alarm.value = 0
                }
                else {
                    holder.switch.setChecked(true)
                    AppData.vibration_alarm.value = 1
                }
                AppData.preferenceHandler.savePreference(AppData.vibration_alarm)
            }
        }
        if (position == 3){
            holder.textViewItem_up.textSize = 14f
            holder.textViewItem_up.setText("Show Tracks")
            holder.textViewItem_down.visibility = View.GONE
            holder.switch.setChecked(if( AppData.show_tracks.value == 1 ) true else false)
            holder.switch.setOnClickListener {
                if( holder.switch.isChecked == true ) AppData.show_tracks.value = 1
                else AppData.show_tracks.value = 0
                AppData.preferenceHandler.savePreference(AppData.show_tracks)
            }
            holder.cardView.setOnClickListener {
                if( holder.switch.isChecked == true ) {
                    holder.switch.setChecked(false)
                    AppData.show_tracks.value = 0
                }
                else {
                    holder.switch.setChecked(true)
                    AppData.show_tracks.value = 1
                }
                AppData.preferenceHandler.savePreference(AppData.show_tracks)
            }
        }
        if (position == 4){
            holder.switch.visibility = View.GONE
            holder.textViewItem_up.textSize = 14f
            holder.textViewItem_up.setText("Distance Unit")
            if(AppData.distance_in_km.value == 1) holder.textViewItem_down.setText("Kilometer")
            else holder.textViewItem_down.setText("Nautical Miles")
            holder.cardView.setOnClickListener {
                context.startActivity(Intent(context, DistanceUnitActivity::class.java))
            }
        }
        if (position == 5){
            holder.switch.visibility = View.GONE
            holder.textViewItem_up.textSize = 14f
            holder.textViewItem_up.setText("Altitude Unit")
            if(AppData.altitude_in_ft.value == 1) holder.textViewItem_down.setText("Feet")
            else holder.textViewItem_down.setText("Meter")
            holder.cardView.setOnClickListener {
                context.startActivity(Intent(context, AltitudeUnitActivity::class.java))
            }
        }
        if (position == 6){
            holder.switch.visibility = View.GONE
            holder.textViewItem_up.textSize = 14f
            holder.textViewItem_up.setText("Restore Default Settings")
            holder.textViewItem_down.visibility = View.GONE
            holder.cardView.setOnClickListener {
                context.startActivity(Intent(context, DefaultSettingActivity::class.java))
            }
        }
    }

    override fun getItemCount(): Int {
        return 7
    }
}