// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.util.Range
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.watchstratux.databinding.ActivityIpAddressBinding

class IpAddressActivity : Activity() {

    private lateinit var binding: ActivityIpAddressBinding

    private fun getColoredSpanned(text: String, color: String): String {
        return "<font color=$color>$text</font>"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ip_address)

        binding = ActivityIpAddressBinding.inflate(getLayoutInflater())
        setContentView(binding.root)

        val ipTextView = findViewById<View>(R.id.IpAddressTextView) as TextView
        val okayButton = findViewById<View>(R.id.ipSettingOkayButton) as Button
        val cancelButton = findViewById<View>(R.id.ipSettingCancelButton) as Button
        val rightButton = findViewById<View>(R.id.ipSettingRightButton) as Button
        val leftButton = findViewById<View>(R.id.ipSettingLeftButton) as Button
        val button_0 = findViewById<View>(R.id.ipSettingButton0) as Button
        val button_1 = findViewById<View>(R.id.ipSettingButton1) as Button
        val button_2 = findViewById<View>(R.id.ipSettingButton2) as Button
        val button_3 = findViewById<View>(R.id.ipSettingButton3) as Button
        val button_4 = findViewById<View>(R.id.ipSettingButton4) as Button
        val button_5 = findViewById<View>(R.id.ipSettingButton5) as Button
        val button_6 = findViewById<View>(R.id.ipSettingButton6) as Button
        val button_7 = findViewById<View>(R.id.ipSettingButton7) as Button
        val button_8 = findViewById<View>(R.id.ipSettingButton8) as Button
        val button_9 = findViewById<View>(R.id.ipSettingButton9) as Button

        var ipAddressPosition = 0

        // convert ip string from 192.168.0.1 to 192.168.000.001
        var ipParts = AppData.preferences.ipAddress.value.split('.').toMutableList()
        for( i in ipParts.indices) ipParts[i] = ipParts[i].padStart(3, '0')
        var ipString = ipParts.toList().joinToString(separator = ".")
        var ipText = ""

        fun updateIpTextView(){
            ipText = getColoredSpanned(ipString.substring(0..(ipAddressPosition-1)), Color.GRAY.toString()) + getColoredSpanned(ipString[ipAddressPosition].toString(), Color.WHITE.toString())
            if( ipAddressPosition < (ipString.length-1) ) ipText += getColoredSpanned(ipString.substring((ipAddressPosition+1)), Color.GRAY.toString())

            ipTextView.setText(Html.fromHtml(ipText))
        }

        updateIpTextView()

        fun cursorForwardInIpTextView(){
            if( ipAddressPosition < (ipString.length-1) ) {
                ipAddressPosition += 1
                when ( ipAddressPosition ) {
                    3 -> ipAddressPosition += 1
                    7 -> ipAddressPosition += 1
                    11 -> ipAddressPosition += 1
                }
            }
        }

        rightButton.setOnClickListener {
            cursorForwardInIpTextView()
            updateIpTextView()
        }

        leftButton.setOnClickListener {
            if( ipAddressPosition > 0 ) {
                ipAddressPosition -= 1
                when( ipAddressPosition ) {
                    3 -> ipAddressPosition -=1
                    7 -> ipAddressPosition -=1
                    11 -> ipAddressPosition -=1
                }
            }
            updateIpTextView()
        }

        button_0.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "0" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_1.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "1" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_2.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "2" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_3.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "3" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_4.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "4" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_5.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "5" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_6.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "6" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_7.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "7" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_8.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "8" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }
        button_9.setOnClickListener {
            ipString = ipString.substring(0..(ipAddressPosition-1)) + "9" + ipString.substring(ipAddressPosition+1)
            cursorForwardInIpTextView()
            updateIpTextView()
        }

        cancelButton.setOnClickListener {
            finish()
        }
        okayButton.setOnClickListener {

            // convert ip string from 192.168.000.001 back to 192.168.0.1
            ipParts = ipString.split('.').toMutableList()
            for( i in ipParts.indices) ipParts[i] = ipParts[i].toInt().toString()
            ipString = ipParts.toList().joinToString(separator = ".")

            AppData.preferences.ipAddress.value = ipString
            AppData.preferenceHandler.savePreference(AppData.preferences.ipAddress)

            startActivity(Intent(applicationContext, IpPortActivity::class.java))
            finish()
        }
    }
}