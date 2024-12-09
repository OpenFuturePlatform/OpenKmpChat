package com.mutualmobile.harvestKmp.android.ui.screens.loginScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils.encryptWithPassword
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils.knownSecret
import com.mutualmobile.harvestKmp.android.ui.utils.SecurityUtils.saveEncryptedSecret
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes

@Composable
fun PinCodeCreationScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = newPin,
            onValueChange = { newPin = it },
            label = { Text("Enter New PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = confirmPin,
            onValueChange = {
                confirmPin = it
            },
            label = { Text("Confirm PIN") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (newPin.length != 4 || confirmPin.length != 4) {
                errorMessage = "PIN must be 4 digits"
                return@Button
            } else if (newPin == confirmPin) {
                errorMessage = null
                val encryptedSecret = encryptWithPassword(knownSecret, newPin)
                println("Pin code encrypted: $encryptedSecret")
                saveEncryptedSecret(context, encryptedSecret)
                println("Pin code saved")
                navController.navigate(HarvestRoutes.Screen.CHAT) // Navigate to main content
            } else {
                errorMessage = "PIN codes do not match"
            }
        }) {
            Text("Set PIN")
        }
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colors.error)
        }
    }


}