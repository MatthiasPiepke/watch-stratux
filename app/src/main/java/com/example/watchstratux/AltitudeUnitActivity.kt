// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.os.Bundle
import android.widget.RadioButton
import com.example.watchstratux.databinding.ActivityAltitudeUnitBinding

class AltitudeUnitActivity : Activity() {

    private lateinit var binding: ActivityAltitudeUnitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAltitudeUnitBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val radio_button_ft = findViewById(R.id.altitude_unit_radioButton_ft) as RadioButton
        val radio_button_m = findViewById(R.id.altitude_unit_radioButton_m) as RadioButton

        if (AppData.altitudeFt.value == 1) {
            radio_button_ft.setChecked(true)
            radio_button_m.setChecked(false)
        } else {
            radio_button_ft.setChecked(false)
            radio_button_m.setChecked(true)
        }

        radio_button_ft.setOnClickListener {
            radio_button_ft.setChecked(true)
            radio_button_m.setChecked(false)
            AppData.altitudeFt.value = 1
            AppData.preferenceHandler.savePreference(AppData.altitudeFt)
        }
        radio_button_m.setOnClickListener {
            radio_button_ft.setChecked(false)
            radio_button_m.setChecked(true)
            AppData.altitudeFt.value = 0
            AppData.preferenceHandler.savePreference(AppData.altitudeFt)
        }
    }
}