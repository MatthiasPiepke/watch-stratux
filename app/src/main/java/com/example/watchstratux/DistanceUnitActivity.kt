// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.os.Bundle
import android.widget.RadioButton
import com.example.watchstratux.databinding.ActivityDistanceUnitBinding

class DistanceUnitActivity : Activity() {

    private lateinit var binding: ActivityDistanceUnitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDistanceUnitBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val radio_button_km = findViewById(R.id.distance_unit_radioButton_km) as RadioButton
        val radio_button_nm = findViewById(R.id.distance_unit_radioButton_nm) as RadioButton

        if (AppData.preferences.distanceKm.value == 1) {
            radio_button_km.setChecked(true)
            radio_button_nm.setChecked(false)
        } else {
            radio_button_km.setChecked(false)
            radio_button_nm.setChecked(true)
        }

        radio_button_km.setOnClickListener {
            radio_button_km.setChecked(true)
            radio_button_nm.setChecked(false)
            AppData.preferences.distanceKm.value = 1
            AppData.preferenceHandler.savePreference(AppData.preferences.distanceKm)
        }
        radio_button_nm.setOnClickListener {
            radio_button_km.setChecked(false)
            radio_button_nm.setChecked(true)
            AppData.preferences.distanceKm.value = 0
            AppData.preferenceHandler.savePreference(AppData.preferences.distanceKm)
        }
    }
}