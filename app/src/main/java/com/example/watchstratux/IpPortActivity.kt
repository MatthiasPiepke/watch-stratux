// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.watchstratux.databinding.ActivityPortBinding

class IpPortActivity : Activity() {

    private lateinit var binding: ActivityPortBinding

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_port)

        binding = ActivityPortBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val portTextView = findViewById<View>(R.id.PortAddressTextView) as TextView
        val okayButton = findViewById<View>(R.id.portSettingOkayButton) as Button
        val cancelButton = findViewById<View>(R.id.portSettingCancelButton) as Button
        val rightButton = findViewById<View>(R.id.portSettingRightButton) as Button
        val leftButton = findViewById<View>(R.id.portSettingLeftButton) as Button
        val button_0 = findViewById<View>(R.id.portSettingButton0) as Button
        val button_1 = findViewById<View>(R.id.portSettingButton1) as Button
        val button_2 = findViewById<View>(R.id.portSettingButton2) as Button
        val button_3 = findViewById<View>(R.id.portSettingButton3) as Button
        val button_4 = findViewById<View>(R.id.portSettingButton4) as Button
        val button_5 = findViewById<View>(R.id.portSettingButton5) as Button
        val button_6 = findViewById<View>(R.id.portSettingButton6) as Button
        val button_7 = findViewById<View>(R.id.portSettingButton7) as Button
        val button_8 = findViewById<View>(R.id.portSettingButton8) as Button
        val button_9 = findViewById<View>(R.id.portSettingButton9) as Button

        var portPosition = 0
        var portString = AppData.ipPort.value.toString().padStart(5, '0')
        var portText = ""

        fun updatePortTextView(){
            portText = getColoredSpanned(portString.substring(0..(portPosition-1)), Color.GRAY.toString()) + getColoredSpanned(portString[portPosition].toString(), Color.WHITE.toString())
            if( portPosition < (portString.length-1) ) portText += getColoredSpanned(portString.substring((portPosition+1)), Color.GRAY.toString())

            portTextView.setText(Html.fromHtml(portText))
        }

        updatePortTextView()

        fun cursorForwardInIpTextView(){
            if( portPosition < (portString.length-1) ) {
                portPosition += 1
                when ( portPosition ) {
                    3 -> portPosition += 1
                    7 -> portPosition += 1
                    11 -> portPosition += 1
                }
            }
        }

        rightButton.setOnClickListener {
            cursorForwardInIpTextView()
            updatePortTextView()
        }

        leftButton.setOnClickListener {
            if( portPosition > 0 ) {
                portPosition -= 1
                when( portPosition ) {
                    3 -> portPosition -=1
                    7 -> portPosition -=1
                    11 -> portPosition -=1
                }
            }
            updatePortTextView()
        }

        button_0.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "0" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_1.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "1" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_2.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "2" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_3.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "3" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_4.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "4" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_5.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "5" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_6.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "6" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_7.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "7" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_8.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "8" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }
        button_9.setOnClickListener {
            portString = portString.substring(0..(portPosition-1)) + "9" + portString.substring(portPosition+1)
            cursorForwardInIpTextView()
            updatePortTextView()
        }

        cancelButton.setOnClickListener {
            finish()
        }
        okayButton.setOnClickListener {
            AppData.ipPort.value = portString.toInt()
            AppData.preferenceHandler.savePreference(AppData.ipPort)
            AppData.stratuxTcpReceiver.stop()
            finish()
        }
    }
}