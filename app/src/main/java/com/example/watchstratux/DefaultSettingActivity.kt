// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.example.watchstratux.databinding.ActivityDefaultSettingBinding

class DefaultSettingActivity : Activity() {

    private lateinit var binding: ActivityDefaultSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDefaultSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val okayButton = findViewById<Button>(R.id.defaultSettingsOkButton)
        val cancelButton = findViewById<Button>(R.id.defaultSettingsCancelButton)

        okayButton.setOnClickListener {
            AppData.preferenceHandler.clearAllPrefences()
            for (pref in DefaultPreferences.defaultPreferences) {
                AppData.preferenceHandler.savePreference(pref)
            }
            AppData.preferenceHandler.loadPreferences(AppData.preferences)
            AppData.stratuxTcpReceiver.stop()
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}