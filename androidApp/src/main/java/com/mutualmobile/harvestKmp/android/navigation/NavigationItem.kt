package com.mutualmobile.harvestKmp.android.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : NavigationItem(HarvestRoutes.Screen.USER_HOME, "Home", Icons.Rounded.Home)
    object Chat : NavigationItem(HarvestRoutes.Screen.CHAT, "Chat", Icons.Rounded.Chat)
    object Task : NavigationItem(HarvestRoutes.Screen.TASK, "Task", Icons.Rounded.Assignment)
    object ChatGPT : NavigationItem(HarvestRoutes.Screen.CHAT_GPT, "Chat GPT", Icons.Rounded.Assistant)
    object
      Contacts : NavigationItem(HarvestRoutes.Screen.ORG_USERS, "Contacts", Icons.Rounded.Contacts)
    object Settings : NavigationItem(HarvestRoutes.Screen.SETTINGS, "Settings", Icons.Rounded.Settings)
    object PinInput : NavigationItem(HarvestRoutes.Screen.PIN_INPUT, "Pin", Icons.Rounded.Build)
}
