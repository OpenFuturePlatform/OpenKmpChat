package com.mutualmobile.harvestKmp.android.ui

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object InactivityManager {
    private const val TIMEOUT: Long = 30000 // 60 seconds of inactivity
    private val handler = Handler(Looper.getMainLooper())
    private var callback: (() -> Unit)? = null

    private val inactivityRunnable = Runnable {
        callback?.invoke()
    }
    fun setCallback(onTimeout: () -> Unit) {
        callback = onTimeout
    }

    fun start() {
        println("InactivityManager: start")
        reset() // Start by resetting the handler
    }

    fun stop() {
        println("InactivityManager: stop")
        handler.removeCallbacks(inactivityRunnable)
    }

    fun reset() {
        println("InactivityManager: reset")
        stop()
        handler.postDelayed(inactivityRunnable, TIMEOUT)
    }
}