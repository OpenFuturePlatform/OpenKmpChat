package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.android.ui.screens.common.rememberQrBitmapPainter
import com.mutualmobile.harvestKmp.android.ui.utils.getIconUrl
import com.mutualmobile.harvestKmp.android.viewmodels.WalletSenderDetailScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import org.koin.androidx.compose.get

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WalletSenderDetailScreen(
    navController: NavHostController,
    userState: OpenDataModel.DataState,
    wsdsVm: WalletSenderDetailScreenViewModel = get(),
    address: String,
    privateKey: String,
    blockchainType: String
) {

    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Receive $blockchainType",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
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
            modifier = Modifier.padding(bodyPadding)
                .then(Modifier.padding(top = 20.dp)),
            //verticalArrangement = Arrangement.Center,
            //horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(25.dp)
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(58.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    painter = getIconUrl(blockchainType),
                    contentDescription = "Icon"
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberQrBitmapPainter(address),
                    contentDescription = "QR Code",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = address,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setText(AnnotatedString(address))
                            Toast.makeText(mContext, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

        }
    }

    LaunchedEffect(wsdsVm.currentNavigationCommand) {
        when (wsdsVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (wsdsVm.currentNavigationCommand as NavigationOpenCommand).screen
                println("WalletSenderDetailScreen Launcher: $destination")

            }
        }
    }

    LaunchedEffect(Unit) {
        when (userState) {
            is OpenDataModel.SuccessState<*> -> {
                //wsdsVm.getWalletDetail(address!!, privateKey!!, blockchainType!!)
            }

            else -> Unit
        }
    }

}