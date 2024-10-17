package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.GenerateWalletDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.HarvestDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.WalletDetailDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.WalletTransactionDialog
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.ExpandableListItem
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.WalletSearchView
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.*
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get


@Composable
fun WalletScreen(
    navController: NavHostController,
    userState: DataState,
    wsVm: WalletScreenViewModel = get(),
) {

    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current
    //val networks = Blockchain.values().map { it.name }
    val networks = listOf("ETH", "BTC", "BNB", "TRX", "SOL")
    val selectedNetworks = remember { mutableStateListOf<String>() }

    LaunchedEffect(wsVm.walletScreenNavigationCommands) {
        when (wsVm.walletScreenNavigationCommands) {
            is NavigationPraxisCommand -> {
                if ((wsVm.walletScreenNavigationCommands as NavigationPraxisCommand).screen.isBlank()) {
                    navController clearBackStackAndNavigateTo HarvestRoutes.Screen.SETTINGS
                }
            }
        }
    }

    LaunchedEffect(userState) {
        while (true) {
            when (userState) {
                is SuccessState<*> -> {
                    wsVm.getUserWallets(userState = userState)
                }

                else -> Unit
            }
            delay(60000)
        }
    }

    if (wsVm.isWalletGenerateDialogVisible) {
        GenerateWalletDialog(
            networks = networks,
            selectedNetworks = selectedNetworks,
            onDismiss = {
                wsVm.isWalletGenerateDialogVisible = false
                wsVm.password = ""
                wsVm.blockchainNetworks = emptyList()
            },
            onConfirm = {
                wsVm.generateWallet()
            }
        )
    }

    if (wsVm.isWalletDetailDialogVisible) {
        WalletDetailDialog(
            onDismiss = {
                wsVm.isWalletDetailDialogVisible = false
                wsVm.currentWalletPrivateKey = ""
                wsVm.currentWalletSeeedPhrases = ""
                wsVm.currentWalletDecryptedPrivateKey = ""
                wsVm.currentWalletAddress = ""
                wsVm.password = ""
            },
            onConfirm = {
                wsVm.decryptWallet()
            },
            onSend = {
                wsVm.isWalletDetailDialogVisible = false
                wsVm.isWalletTransactionDialogVisible = true
            },
            wsVm = wsVm
        )
    }
    if (wsVm.isWalletTransactionDialogVisible) {
        WalletTransactionDialog(
            onDismiss = {
                wsVm.isWalletTransactionDialogVisible = false
                wsVm.currentWalletPrivateKey = ""
                wsVm.currentWalletSeeedPhrases = ""
                wsVm.currentWalletDecryptedPrivateKey = ""
                wsVm.currentWalletAddress = ""
                wsVm.password = ""
                wsVm.currentBroadcastError = ""
                wsVm.currentBroadcastHash = ""
                wsVm.isBroadcastLoading = false
            },
            onConfirm = {
                wsVm.isBroadcastLoading = true
                wsVm.broadcastTransaction(wsVm.currentWalletAddress, wsVm.currentWalletDecryptedPrivateKey, wsVm.currentReceiverAddress, wsVm.currentReceiverAmount)
            },
            wsVm = wsVm
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = MR.strings.choose_wallet.resourceId),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    WalletSearchView(wsVm.textState) { updatedState ->
                        wsVm.textState = updatedState
                    }
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        floatingActionButton = { GenerateWalletButtonCompose(mContext, navController, wsVm) },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        scaffoldState = scaffoldState

    ) { bodyPadding ->

        var expandedIndex by remember { mutableStateOf(-1) }

        Column(modifier = Modifier.padding(bodyPadding)) {
            AnimatedVisibility(visible = wsVm.currentWalletScreenState is LoadingState) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                val searchedText = wsVm.textState.text
                wsVm.filteredWalletListMap = if (searchedText.isEmpty()) {
                    wsVm.wallets.sortedBy { it.blockchainType }
                } else {
                    wsVm.wallets.filter { it.blockchainType?.contains(searchedText, true) == true }.sortedBy { it.blockchainType }
                }

                var groupedWallets: Map<String, List<WalletResponse>> =
                    wsVm.filteredWalletListMap.groupBy { it.blockchainType!! }
                // ADD USDT Wallets
                val usdtWallets =
                    wsVm.filteredWalletListMap.filter { it.blockchainType!! == "ETH" || it.blockchainType!! == "TRX" }
                groupedWallets = groupedWallets.plus("USDT" to usdtWallets)

                itemsIndexed(groupedWallets.entries.toList()) { index, blockchainWallet ->

                    ExpandableListItem(
                        blockchainType = blockchainWallet.key,
                        wallets = blockchainWallet.value,
                        isExpanded = expandedIndex == index,
                        onClick = {
                            expandedIndex = if (expandedIndex == index) -1 else index
                        },
                        wsVm = wsVm
                    )

                }

            }
        }
        HarvestDialog(praxisCommand = wsVm.walletScreenNavigationCommands, onConfirm = {
            wsVm.walletScreenNavigationCommands = null
        })
    }
}

@Composable
fun GenerateWalletButtonCompose(context: Context, navController: NavHostController, wsVm: WalletScreenViewModel) {

//    OutlinedButton(
//        onClick = { Toast.makeText(context, "This is a Circular Button with a + Icon", Toast.LENGTH_LONG).show()},
//        modifier= Modifier.size(30.dp),
//        shape = CircleShape,
//        border= BorderStroke(1.dp, Color(0XFF0F9D58)),
//        contentPadding = PaddingValues(0.dp),
//        colors = ButtonDefaults.outlinedButtonColors(contentColor =  Color.Blue)
//    ) {
//        // Adding an Icon "Add" inside the Button
//        Icon(Icons.Default.Add ,contentDescription = "content description", tint= Color(0XFF0F9D58))
//    }

    FloatingActionButton(
        shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 40)),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        onClick = { wsVm.isWalletGenerateDialogVisible = true }
        //onClick = { Toast.makeText(context, "This will generate wallet", Toast.LENGTH_SHORT).show()},
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}