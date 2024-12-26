package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.qrcode.QRCodeReader
import com.mutualmobile.harvestKmp.android.ui.screens.common.WalletReceiverDecryptDialog
import com.mutualmobile.harvestKmp.android.viewmodels.BiometricViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.WalletReceiverDetailScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import org.koin.androidx.compose.get

@SuppressLint("StateFlowValueCalledInComposition")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WalletReceiverDetailScreen(
    navController: NavHostController,
    userState: OpenDataModel.DataState,
    wrdsVm: WalletReceiverDetailScreenViewModel = get(),
    address: String?,
    privateKey: String?,
    blockchainType: String?
) {

    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val txtFieldError = remember { mutableStateOf("") }
    val addressFieldError = remember { mutableStateOf("") }
    val amountFieldError = remember { mutableStateOf("") }

    var addressField by remember { mutableStateOf(TextFieldValue("0x014D9Fcdb245CF31BfbaD92F3031FE036fE91Bc3")) }
    var amountField by remember { mutableStateOf(TextFieldValue("0.0001")) }

    showDialogs(wrdsVm)

    val biometricViewModel: BiometricViewModel = viewModel()

    // TODO - use in login screen
    val context = LocalContext.current as FragmentActivity
    val biometricManager = BiometricManager.from(context)
    val canAuthenticateWithBiometrics = when (biometricManager.canAuthenticate()) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> {
            Log.e("TAG", "Device does not support strong biometric authentication")
            false
        }
    }

    val biometricPrompt = BiometricPrompt(
        context,
        mContext.mainExecutor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                Log.e("TAG", "onAuthenticationError")
                biometricViewModel.isAuthenticated.value = false
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                Log.d("TAG", "Authentication successful!!!")
                biometricViewModel.isAuthenticated.value = true
                wrdsVm.currentBroadcastHash = ""
            }

            override fun onAuthenticationFailed() {
                Log.e("TAG", "onAuthenticationFailed")
                biometricViewModel.isAuthenticated.value = false
            }
        }
    )
    biometricViewModel.setBiometricPrompt(biometricPrompt)
    biometricViewModel.setBiometricManager(biometricManager)

    val qrScanLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val contents = IntentIntegrator.parseActivityResult(result.resultCode, intent).contents
            contents?.let { addressField = TextFieldValue(it) }
        }
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val bitmap = BitmapFactory.decodeStream(mContext.contentResolver.openInputStream(it))
            val result = decodeQRCode(bitmap)
            result?.let { addressField = TextFieldValue(it.text) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Send $blockchainType",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        wrdsVm.currentWalletDecryptedPrivateKey = ""
                        wrdsVm.password = ""
                        navController.navigate(
                            HarvestRoutes.Screen.WALLET_DETAIL.withWalletDetail(
                                address,
                                blockchainType,
                                privateKey
                            )
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        scaffoldState = scaffoldState

    ) { bodyPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bodyPadding).then(Modifier.padding(10.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (biometricViewModel.isAuthenticated.value) {
                wrdsVm.decryptWallet()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (txtFieldError.value.isNotEmpty() || wrdsVm.currentBroadcastError.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "",
                            tint = colorResource(R.color.darker_gray),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                        )
                        Text(
                            text = txtFieldError.value,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = wrdsVm.currentBroadcastError,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = addressField,
                    onValueChange = { addressField = it },
                    label = { Text(text = "Enter Address") },
                    trailingIcon = {
                        Row {

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    //.background(Color.Gray, RectangleShape)
                                    .clickable {
                                        IntentIntegrator(mContext as Activity).apply {
                                            setPrompt("Scan a QR code")
                                            setOrientationLocked(true)
                                            qrScanLauncher.launch(createScanIntent())
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_menu_camera),
                                    contentDescription = "Scan",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    //.background(Color.Gray, RectangleShape)
                                    .clickable {
                                        galleryLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_menu_gallery),
                                    contentDescription = "Scan",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountField,
                    onValueChange = { amountField = it },
                    label = { Text(text = "Enter Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.width(16.dp).height(40.dp))

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    if (wrdsVm.isBroadcastLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(2.dp))
                    } else {
                        if (wrdsVm.currentBroadcastHash.isEmpty()) {

                            Button(onClick = {
                                if (addressField.text.isEmpty()) {
                                    txtFieldError.value = "Address can not be empty"
                                    addressFieldError.value = "Address can not be empty"
                                    return@Button
                                }
                                if (amountField.text.isEmpty()) {
                                    txtFieldError.value = "Amount can not be empty"
                                    amountFieldError.value = "Amount can not be empty"
                                    return@Button
                                }
                                wrdsVm.broadcastTransaction(addressField.text, amountField.text)
                            }) {
                                Text(text = "Next")
                            }
                        } else {
                            Button(onClick = {
                                clipboardManager.setText(AnnotatedString((wrdsVm.currentBroadcastHash)))
                            }) {
                                Text(text = "Copy ID")
                            }
                        }

                    }
                }
            } else {
                // Trigger biometric authentication
                biometricViewModel.authenticate()
            }
//            else {
//                Spacer(modifier = Modifier.width(4.dp))
//                Row(
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Button(onClick = {
//                        wrdsVm.isWalletDecryptDialogVisible = true
//                    }) {
//                        Text(text = "Encrypt private key")
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                }
//            }
        }

    }

    LaunchedEffect(wrdsVm.currentNavigationCommand) {
        when (wrdsVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (wrdsVm.currentNavigationCommand as NavigationOpenCommand).screen
                println("WalletReceiverDetailScreen Launcher: $destination")
            }
        }
    }

    LaunchedEffect(Unit) {
        when (userState) {
            is OpenDataModel.SuccessState<*> -> {
                wrdsVm.getWalletDetail(address!!, privateKey!!, blockchainType!!)
            }
            else -> Unit
        }
    }

}

@Composable
fun showDialogs(wrdsVm: WalletReceiverDetailScreenViewModel) {

    if (wrdsVm.isWalletDecryptDialogVisible) {
        WalletReceiverDecryptDialog(
            onDismiss = {
                wrdsVm.isWalletDecryptDialogVisible = false
                wrdsVm.currentWalletDecryptedPrivateKey = ""
                wrdsVm.password = ""
            },
            onConfirm = {
                wrdsVm.decryptWallet()
            },
            wrdsm = wrdsVm
        )
    }

}

fun decodeQRCode(bitmap: android.graphics.Bitmap): Result? {
    val width = bitmap.width
    val height = bitmap.height
    val intArray = IntArray(width * height)
    bitmap.getPixels(intArray, 0, width, 0, 0, width, height)

    val source: LuminanceSource = RGBLuminanceSource(width, height, intArray)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

    return try {
        QRCodeReader().decode(binaryBitmap)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}