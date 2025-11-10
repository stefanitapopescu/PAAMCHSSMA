package com.example.paamchssma.controllers

import android.app.Activity

/**
 * ARController - DISABLED
 * AR functionality is not used in this app
 */
class ARController(private val activity: Activity) {
    fun checkARSupport(): Boolean = false
    fun onResume() {}
    fun onPause() {}
    fun cleanup() {}
}
