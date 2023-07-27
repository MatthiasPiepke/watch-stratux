// Copyright (c) 2023 Matthias Piepke
//
// Redistribution and use under the terms of The "BSD New" License,
// which can be found in the LICENSE file, herein included as part of this header.

package com.example.watchstratux

import android.os.Handler
import android.os.Looper
import android.util.Log

class Timeout(private val timeoutTime: Int, private val id: String) {
    var isTimeout = false
    private var reset = false
    private var stopTimeout = false
    private val timeoutHandler = Handler(Looper.getMainLooper())

    private val TAG = "TimeOut"

    private val timeoutRunnable = object : Runnable {
        override fun run() {
            //Log.i(TAG, "check " + id)
            if (reset == true) {
                Log.e(TAG, id + " Timeout Event")
                isTimeout = true
                listener.OnTimeoutEvent()
            } else {
                reset = true
            }
            if (stopTimeout == false) timeoutHandler.postDelayed(this, timeoutTime.toLong())
        }
    }

    fun reset() {
        reset = false
        //Log.i(TAG, "reset " + id)
    }

    fun start() {
        isTimeout = false
        reset = true
        stopTimeout = false
        timeoutHandler.postDelayed(timeoutRunnable, timeoutTime.toLong())
        //Log.i(TAG, id + " started")
    }

    fun stop() {
        stopTimeout = true
        timeoutHandler.removeCallbacks(timeoutRunnable)
        //Log.i(TAG, id + " stopped")
    }

    interface TimeoutEventListener {
        fun OnTimeoutEvent()
    }

    internal inner class DefaultTimeoutEventListener : TimeoutEventListener {
        override fun OnTimeoutEvent() {
            Log.e(TAG, id + " default Timeout Event")
            stop()
        }
    }

    private var listener: TimeoutEventListener = DefaultTimeoutEventListener()

    fun RegisterTimeoutEventListener(listener: TimeoutEventListener) {
        this.listener = listener
    }
}