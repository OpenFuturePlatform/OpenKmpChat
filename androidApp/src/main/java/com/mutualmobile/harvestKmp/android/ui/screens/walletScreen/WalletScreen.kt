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
import com.mutualmobile.harvestKmp.android.ui.screens.common.*
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.ExpandableListItem
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.WalletSearchView
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.WalletsScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.*
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get


@Composable
fun WalletScreen(
    navController: NavHostController,
    userState: DataState,
    wsVm: WalletsScreenViewModel = get(),
) {

    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current
    //val networks = Blockchain.values().map { it.name }
    val networks = listOf("ETH", "BTC", "BNB", "TRX", "SOL")
    val selectedNetworks = remember { mutableStateListOf<String>() }

    HandleNavigationCommands(navController, wsVm)
    HandleUserState(userState, wsVm)

    ShowDialogs(wsVm, networks, selectedNetworks)

    Scaffold(
        topBar = {
            WalletTopAppBar(navController, wsVm)
        },
        floatingActionButton = { GenerateWalletButtonCompose(mContext, navController, wsVm) },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        scaffoldState = scaffoldState

    ) { bodyPadding ->
        WalletContent(bodyPadding, wsVm)
    }
}


@Composable
fun HandleNavigationCommands(navController: NavHostController, wsVm: WalletsScreenViewModel) {
    LaunchedEffect(wsVm.currentNavigationCommand) {
        when (wsVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (wsVm.currentNavigationCommand as NavigationOpenCommand).screen
                wsVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }
}

@Composable
fun HandleUserState(userState: DataState, wsVm: WalletsScreenViewModel) {
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
}

@Composable
fun ShowDialogs(wsVm: WalletsScreenViewModel, networks: List<String>, selectedNetworks: MutableList<String>) {
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
                wsVm.isWalletDecryptDialogVisible = false
                wsVm.currentWalletPrivateKey = ""
                wsVm.currentWalletSeeedPhrases = ""
                wsVm.currentWalletDecryptedPrivateKey = ""
                wsVm.currentWalletAddress = ""
                wsVm.password = ""
            },
            wsVm = wsVm
        )
    }

    if (wsVm.isWalletDecryptDialogVisible) {
        WalletDecryptDialog(
            onDismiss = {
                wsVm.isWalletDecryptDialogVisible = false
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
                wsVm.isWalletDecryptDialogVisible = false
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
}

@Composable
fun WalletTopAppBar(navController: NavHostController, wsVm: WalletsScreenViewModel) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = MR.strings.choose_wallet.resourceId),
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigate(HarvestRoutes.Screen.SETTINGS) }) {
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
}

@Composable
fun WalletContent(bodyPadding: PaddingValues, wsVm: WalletsScreenViewModel) {
    var expandedIndex by remember { mutableStateOf(-1) }

    Column(modifier = Modifier.padding(bodyPadding)) {
        AnimatedVisibility(visible = wsVm.currentWalletsScreenState is LoadingState) {
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
                wsVm.filteredWalletListMap.filter { it.blockchainType!! == "ETH" || it.blockchainType!! == "TRX" || it.blockchainType!! == "SOL" }

            if (usdtWallets.isNotEmpty())
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

    HarvestDialog(openCommand = wsVm.currentNavigationCommand, onConfirm = {
        wsVm.currentNavigationCommand = null
    })
}

@Composable
fun GenerateWalletButtonCompose(context: Context, navController: NavHostController, wsVm: WalletsScreenViewModel) {

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