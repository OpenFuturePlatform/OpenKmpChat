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
    object Chat : NavigationItem(HarvestRoutes.Screen.CHAT, "Chat", Icons.Rounded.Send)
    object Task : NavigationItem(HarvestRoutes.Screen.TASK, "Task", Icons.Rounded.List)
    object ChatGPT : NavigationItem(HarvestRoutes.Screen.CHAT_GPT, "Chat GPT", Icons.Rounded.Search)
    object
      Contacts : NavigationItem(HarvestRoutes.Screen.ORG_USERS, "Contacts", Icons.Rounded.AccountBox)
    object Settings : NavigationItem(HarvestRoutes.Screen.SETTINGS, "Settings", Icons.Rounded.Settings)
}
