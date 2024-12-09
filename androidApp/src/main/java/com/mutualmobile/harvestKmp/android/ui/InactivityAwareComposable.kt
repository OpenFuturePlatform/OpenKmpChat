package com.mutualmobile.harvestKmp.android.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun InactivityAwareComposable(content: @Composable () -> Unit) {
    var lastActivityTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(lastActivityTime) {
        InactivityManager.reset()
    }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures {
                lastActivityTime = System.currentTimeMillis()
                println("InactivityAwareComposable: User activity detected at : $lastActivityTime")
                InactivityManager.reset() // Reset the inactivity timer on any tap gesture
            }
        }
    ) {
        content()
    }
}
