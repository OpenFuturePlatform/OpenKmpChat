package com.mutualmobile.harvestKmp.android.ui.screens.loginScreen

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mutualmobile.harvestKmp.android.ui.InactivityManager
import com.mutualmobile.harvestKmp.android.ui.MainActivity
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.LoginViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.UserListViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import org.koin.androidx.compose.get

@Composable
fun UnlockScreen(
    navController: NavHostController,
    lVm: LoginViewModel = get(),
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    //var isAuthenticated by remember { mutableStateOf(false) }
    var isAuthenticated = sharedPreferences.getBoolean("isAuthenticated", false)
    println(
        "called UnlockScreen and isAuthenticated is $isAuthenticated and sharedPreferences isAuthenticated is ${
            sharedPreferences.getBoolean(
                "isAuthenticated",
                false
            )
        } and isPinSet is ${sharedPreferences.getBoolean("isPinSet", false)}"
    )

    LaunchedEffect(lVm.currentNavigationCommand) {
        println("unlock nav command")
        when (lVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (lVm.currentNavigationCommand as NavigationOpenCommand).screen
                lVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    OpenChatTheme {
        Surface {

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
                print("UnlockScreen not authenticated - ")
                if (SecurityUtils.isPinSet(context)) {
                    println("Pin set")
                    PinInputColumn(
                        isPinCorrect = { pin ->
                            println("Is pin correct: $pin")
                            isAuthenticated = pin
                        },
                        navController = navController
                    )
                } else {
                    println("Pin not set")
                    if (isAuthenticated) {
                        PinCodeCreationScreen(navController)
                    } else {
                        println("navigate to login")
                        navController.navigate(HarvestRoutes.Screen.LOGIN)
                    }
                }
            }

        }
    }
}