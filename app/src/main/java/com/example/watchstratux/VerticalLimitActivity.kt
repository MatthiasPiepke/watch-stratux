// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import com.example.watchstratux.databinding.ActivityVerticalLimitBinding

class VerticalLimitActivity : Activity() {

    private lateinit var binding: ActivityVerticalLimitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerticalLimitBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val okayButton = findViewById<View>(R.id.verticalLimitSettingsButton) as Button

        val np1 = findViewById<View>(R.id.verticalLimitSettingsPicker1) as NumberPicker
        val np2 = findViewById<View>(R.id.verticalLimitSettingsPicker2) as NumberPicker

        np1.minValue = 0
        np1.maxValue = AppData.lowerLimitValues.size - 1
        np1.displayedValues = AppData.lowerLimitValues
        np1.value = AppData.lower_vertical_limit.value

        np2.minValue = 0
        np2.maxValue = AppData.upperLimitValues.size - 1
        np2.displayedValues = AppData.upperLimitValues
        np2.value = AppData.upper_vertical_limit.value


        okayButton.setOnClickListener {
            AppData.lower_vertical_limit.value = np1.value
            AppData.upper_vertical_limit.value = np2.value
            AppData.preferenceHandler.savePreference(AppData.lower_vertical_limit)
            AppData.preferenceHandler.savePreference(AppData.upper_vertical_limit)
            finish()
        }
    }
}