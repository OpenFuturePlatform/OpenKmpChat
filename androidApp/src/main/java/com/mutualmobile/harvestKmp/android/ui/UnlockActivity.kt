package com.mutualmobile.harvestKmp.android.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.UnlockScreen

class UnlockActivity : FragmentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("UnlockActivity.onCreate")
        setContent {
            UnlockScreen(rememberNavController())
        }
    }
}