package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen

import android.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.android.ui.screens.common.*
import com.mutualmobile.harvestKmp.android.ui.screens.common.WalletDecryptDialog
import com.mutualmobile.harvestKmp.android.ui.utils.getIconUrl
import com.mutualmobile.harvestKmp.android.viewmodels.WalletReceiverDetailScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.WalletsScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import org.koin.androidx.compose.get

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
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    val txtFieldError = remember { mutableStateOf("") }
    val addressField = remember { mutableStateOf("") }
    val amountField = remember { mutableStateOf("") }

    ShowDialogs(wrdsVm)

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
        Column(modifier = Modifier.padding(bodyPadding).then(Modifier.padding(10.dp))) {

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

            Spacer(modifier = Modifier.height(20.dp))

            if (wrdsVm.currentWalletDecryptedPrivateKey != "") {

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                color = colorResource(id = if (txtFieldError.value.isEmpty()) R.color.darker_gray else R.color.holo_red_dark)
                            )
                        ),
                    label = { Text("Receiver") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    value = "0x014D9Fcdb245CF31BfbaD92F3031FE036fE91Bc3",
                    onValueChange = {
                        addressField.value = it
                    })

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(
                                width = 2.dp,
                                color = colorResource(id = if (txtFieldError.value.isEmpty()) R.color.darker_gray else R.color.holo_red_dark)
                            )
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    placeholder = { Text(text = "Enter amount") },
                    label = { Text("Amount") },
                    value = amountField.value,
                    onValueChange = {
                        amountField.value = it
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                                if (addressField.value.isEmpty()) {
                                    txtFieldError.value = "Address can not be empty"
                                    return@Button
                                }
                                if (amountField.value.isEmpty()) {
                                    txtFieldError.value = "Amount can not be empty"
                                    return@Button
                                }
                                wrdsVm.broadcastTransaction(addressField.value, amountField.value)
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
                Spacer(modifier = Modifier.width(4.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        wrdsVm.isWalletDecryptDialogVisible = true
                    }) {
                        Text(text = "Encrypt private key")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
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
fun ShowDialogs(wrdsVm: WalletReceiverDetailScreenViewModel) {

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