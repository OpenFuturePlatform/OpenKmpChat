package com.mutualmobile.harvestKmp.android.ui.screens.loginScreen

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.mutualmobile.harvestKmp.android.ui.InactivityManager
import com.mutualmobile.harvestKmp.android.ui.MainActivity
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes

@Composable
fun UnlockScreen(navController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    var isAuthenticated by remember { mutableStateOf(false) }
    //var isAuthenticated = sharedPreferences.getBoolean("isAuthenticated", false)
    println("called UnlockScreen and isAuthenticated is $isAuthenticated and sharedPreferences is ${sharedPreferences.getBoolean("isAuthenticated", false)}")

    if (isAuthenticated) {
        println("UnlockScreen authenticated, continuing to context activity")
        // Finish this activity and return to the previous screen
        LaunchedEffect(Unit) {
            sharedPreferences.edit().putBoolean("isAuthenticated", true).apply()
            //(context as? ComponentActivity)?.finish()
            // Navigate to HomeScreen
            context.startActivity(Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    } else {
        println("UnlockScreen not authenticated, showing pin input")
        if (SecurityUtils.isPinSet(context)) {
            PinInputColumn(
                isPinCorrect = { pin ->
                    println("Is pin correct: $pin")
                    isAuthenticated = pin
                },
                navController = navController
            )
        } else {
            println("Pin not set")
            PinCodeCreationScreen(navController)
        }
    }


}