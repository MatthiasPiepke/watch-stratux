// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.app.Activity
import android.os.Bundle
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.watchstratux.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsRecyclerViewAdapter: SettingsRecyclerViewAdapter
    lateinit var mWearableRecyclerView: WearableRecyclerView
    var pos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mWearableRecyclerView = findViewById<WearableRecyclerView>(R.id.settings_menu_view)
        val mLayoutManager = WearableLinearLayoutManager(this, CustomScrollingLayoutCallback())
        mWearableRecyclerView.layoutManager = mLayoutManager
        mWearableRecyclerView.isEdgeItemsCenteringEnabled = true

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(mWearableRecyclerView)

        mWearableRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) pos = mLayoutManager.getPosition(snapHelper.findSnapView(mLayoutManager)!!)
            }
        })

        settingsRecyclerViewAdapter = SettingsRecyclerViewAdapter(this)
    }

    override fun onResume() {
        super.onResume()
        mWearableRecyclerView.scrollToPosition(pos)
        mWearableRecyclerView.adapter = settingsRecyclerViewAdapter
    }
}