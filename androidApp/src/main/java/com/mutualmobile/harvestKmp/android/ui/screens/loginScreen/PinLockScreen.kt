package com.mutualmobile.harvestKmp.android.ui.screens.loginScreen

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.mutualmobile.harvestKmp.android.R
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.BiometricViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.PinInputViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import org.koin.androidx.compose.get

@Composable
fun PinLockScreen(
    navController: NavHostController,
    pIVm: PinInputViewModel = get()
) {
    val ctx = LocalContext.current
    println("PinLockScreen called")
    val biometricViewModel: BiometricViewModel = viewModel()

    LaunchedEffect(pIVm.currentNavigationCommand) {
        when (pIVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (pIVm.currentNavigationCommand as NavigationOpenCommand).screen
                println("Lock screen destination: $destination")
                pIVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    PinLockInputScreen(pIVm, ctx, biometricViewModel, navController)
}

const val pinSize = 4

@Composable
fun PinLockInputScreen(
    pIVm: PinInputViewModel,
    context: Context,
    biometricViewModel: BiometricViewModel,
    navController: NavHostController
) {
    val inputPin = remember { mutableStateListOf<Int>() }
    val error = remember { mutableStateOf("") }
    val showSuccess = remember { mutableStateOf(false) }
    var isAuthenticated by remember { mutableStateOf(false) }
    var showBiometricPrompt by remember { mutableStateOf(false) }

    // Initialize the biometric manager and prompt
    val biometricManager = BiometricManager.from(context)
    val canAuthenticateWithBiometrics = when (biometricManager.canAuthenticate()) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> {
            Log.e("TAG", "Device does not support strong biometric authentication")
            false
        }
    }
    val biometricPrompt = BiometricPrompt(
        LocalContext.current as FragmentActivity,
        context.mainExecutor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e("TAG", "onAuthenticationError")
                isAuthenticated = false
                pIVm.isAuthenticated = false
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Log.d("TAG", "Authentication successful!!!")
                isAuthenticated = true
                pIVm.isAuthenticated = true
            }

            override fun onAuthenticationFailed() {
                Log.e("TAG", "onAuthenticationFailed")
                isAuthenticated = false
                pIVm.isAuthenticated = false
            }
        }
    )
    biometricViewModel.setBiometricPrompt(biometricPrompt)
    biometricViewModel.setBiometricManager(biometricManager)

    if (isAuthenticated) {
        showBiometricPrompt = false
        showSuccess.value = true
    }

    if (inputPin.size == 4) {

        //LaunchedEffect(true) {
        //delay(3000)

        if (!pIVm.isPinSet(context)) {
            navController.navigate(HarvestRoutes.Screen.PIN_CREATE)
        } else if (pIVm.checkPin(context, inputPin.joinToString(""))) {
            showSuccess.value = true
            error.value = ""
            isAuthenticated = true
            pIVm.isAuthenticated = true
        } else {
            inputPin.clear()
            error.value = "Wrong pin, Please retry!"
            isAuthenticated = false
        }
        //}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .systemBarsPadding()
            //.padding(top = 20.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Image(
                    painter = painterResource(id = R.drawable.btc),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(30.dp))

                if (showSuccess.value) {
                    ComposeLottieAnimation(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        isSuccess = true,
                        isFailed = false
                    )
                } else {
                    Row {
                        (0 until pinSize).forEach {
                            Icon(
                                imageVector = if (inputPin.size + 1 > it) Icons.Default.Circle else Icons.Outlined.Circle,
                                contentDescription = it.toString(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(30.dp),
                                tint = Color.Black
                            )
                        }
                    }
                }

                if (error.value.isNotEmpty()) {
                    ComposeLottieAnimation(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(15.dp),
                        isSuccess = false,
                        isFailed = true
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

            }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..3).forEach {
                        PinKeyItem(
                            onClick = { inputPin.add(it) }
                        ) {
                            Text(
                                text = it.toString(),
                                style = typography.h5
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (4..6).forEach {
                        PinKeyItem(
                            onClick = { inputPin.add(it) }
                        ) {
                            Text(
                                text = it.toString(),
                                style = typography.h5
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (7..9).forEach {
                        PinKeyItem(
                            onClick = { inputPin.add(it) }
                        ) {
                            Text(
                                text = it.toString(),
                                style = typography.h5,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Biometric",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                // Trigger biometric authentication
                                biometricViewModel.authenticate()
                            }
                    )
                    PinKeyItem(
                        onClick = { inputPin.add(0) },
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text(
                            text = "0",
                            style = typography.h5,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.Backspace,
                        contentDescription = "Clear",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                if (inputPin.isNotEmpty()) {
                                    inputPin.removeLast()
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun PinInputColumn(isPinCorrect: (Boolean) -> Unit, navController: NavHostController) {

    println("PinInputColumn called")

    val context = LocalContext.current
    val biometricViewModel: BiometricViewModel = viewModel()
    val pIVm: PinInputViewModel = get()

    //val navController = rememberNavController()

    var isAuthenticated by remember { mutableStateOf(false) }
    val inputPin = remember { mutableStateListOf<Int>() }
    val error = remember { mutableStateOf("") }

    // Initialize the biometric manager and prompt
    val biometricManager = BiometricManager.from(context)
    val canAuthenticateWithBiometrics = when (biometricManager.canAuthenticate()) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> {
            Log.e("TAG", "Device does not support strong biometric authentication")
            false
        }
    }
    val biometricPrompt = BiometricPrompt(
        LocalContext.current as FragmentActivity,
        context.mainExecutor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e("TAG", "onAuthenticationError")
                isAuthenticated = false
                isPinCorrect(false)
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Log.d("TAG", "Authentication successful!!!")
                isAuthenticated = true
                isPinCorrect(true)
            }

            override fun onAuthenticationFailed() {
                Log.e("TAG", "onAuthenticationFailed")
                isAuthenticated = false
                isPinCorrect(false)
            }
        }
    )
    biometricViewModel.setBiometricPrompt(biometricPrompt)
    biometricViewModel.setBiometricManager(biometricManager)

    if (inputPin.size == 4) {

        if (!pIVm.isPinSet(context)) {
            navController.navigate(HarvestRoutes.Screen.PIN_CREATE)
        } else if (pIVm.checkPin(context, inputPin.joinToString(""))) {
            isAuthenticated = true
            pIVm.isAuthenticated = true
            isPinCorrect(true)
        } else {
            inputPin.clear()
            error.value = "Wrong pin, Please retry!"
            isAuthenticated = false
            isPinCorrect(false)
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .systemBarsPadding()
            //.padding(top = 20.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(50.dp))

                Image(
                    painter = painterResource(id = R.drawable.btc),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row {
                        (0 until pinSize).forEach {
                            Icon(
                                imageVector = if (inputPin.size > it) Icons.Default.Circle else Icons.Outlined.Circle,
                                contentDescription = it.toString(),
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(30.dp),
                                tint = Color.Black
                            )
                        }
                    }


                if (error.value.isNotEmpty()) {
                    ComposeLottieAnimation(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(15.dp),
                        isSuccess = false,
                        isFailed = true
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

            }

            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..3).forEach {
                        PinKeyItem(
                            onClick = { inputPin.add(it) }
                        ) {
                            Text(
                                text = it.toString(),
                                style = typography.h5
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (4..6).forEach {
                        PinKeyItem(
                            onClick = { inputPin.add(it) }
                        ) {
                            Text(
                                text = it.toString(),
                                style = typography.h5
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (7..9).forEach {
                        PinKeyItem(
                            onClick = { inputPin.add(it) }
                        ) {
                            Text(
                                text = it.toString(),
                                style = typography.h5,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (canAuthenticateWithBiometrics) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric",
                            modifier = Modifier
                                .size(25.dp)
                                .clickable {
                                    // Trigger biometric authentication
                                    biometricViewModel.authenticate()
                                }
                        )
                    }

                    PinKeyItem(
                        onClick = { inputPin.add(0) },
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        Text(
                            text = "0",
                            style = typography.h5,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.Backspace,
                        contentDescription = "Clear",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                if (inputPin.isNotEmpty()) {
                                    inputPin.removeLast()
                                }
                            }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PinKeyItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.padding(8.dp),
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        onClick = onClick,
        elevation = elevation,
    ) {
        CompositionLocalProvider(
            LocalContentAlpha provides contentColor.alpha
        ) {
            ProvideTextStyle(
                typography.h5
            ) {
                Box(
                    modifier = Modifier.defaultMinSize(minWidth = 64.dp, minHeight = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ComposeLottieAnimation(modifier: Modifier, isSuccess: Boolean, isFailed: Boolean) {

    val clipSpecs = LottieClipSpec.Progress(
        min = if (isFailed) 0.499f else 0.0f,
        max = if (isSuccess) 0.44f else if (isFailed) 0.95f else 0.282f
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_loading_success_failed))

    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = if (isSuccess || isFailed) 1 else LottieConstants.IterateForever,
        clipSpec = clipSpecs,
    )
}

@Composable
fun PinInputOldColumn(pIVm: PinInputViewModel, context: Context) {

    var pinCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display PIN circles
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(if (index < pinCode.length) Color.Black else Color.Gray)
                )
            }
        }

        // Numeric keypad
        val buttons = listOf(
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "DEL", "0", "OK"
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            for (i in buttons.indices step 3) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    for (j in 0..2) {
                        val buttonLabel = buttons[i + j]
                        Button(
                            onClick = {
                                when (buttonLabel) {
                                    "DEL" -> if (pinCode.isNotEmpty()) pinCode = pinCode.dropLast(1)
                                    "OK" -> if (pinCode.length == 4) pIVm.onPinCodeChanged(context, pinCode)
                                    else -> if (pinCode.length < 4) pinCode += buttonLabel
                                }
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            if (buttonLabel == "DEL") {
                                Text("⌫", fontSize = 24.sp)
                            } else {
                                Text(buttonLabel, fontSize = 24.sp)
                            }
                        }

//                        AnimatedCircleButton(
//                            onClick = {
//                                when (buttonLabel) {
//                                    "⌫" -> if (pinCode.isNotEmpty()) pinCode = pinCode.dropLast(1)
//                                    "OK" -> if (pinCode.length == 4) pIVm.onPinCodeChanged(context, pinCode)
//                                    else -> if (pinCode.length < 4) pinCode += buttonLabel
//                                }
//                            }
//                        )

                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCircleButton(onClick: () -> Unit) {

    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.Blue)
            .scale(scale.value)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "", color = Color.White)
    }
}