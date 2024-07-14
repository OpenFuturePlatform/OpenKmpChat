package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.FloatingActionButtonCompose
import com.mutualmobile.harvestKmp.android.ui.screens.common.HarvestDialog
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.WalletListItem
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.WalletSearchView
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.NewEntryScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.*
import org.koin.androidx.compose.get


@Composable
fun WalletScreen(
    navController: NavHostController,
    nesVm: NewEntryScreenViewModel = get(),
    userState: DataState,
    psVm: WalletScreenViewModel = get(),
) {

    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current

    LaunchedEffect(psVm.walletScreenNavigationCommands) {
        when (psVm.walletScreenNavigationCommands) {
            is NavigationPraxisCommand -> {
                if ((psVm.walletScreenNavigationCommands as NavigationPraxisCommand).screen.isBlank()) {
                    navController clearBackStackAndNavigateTo HarvestRoutes.Screen.FIND_WORKSPACE
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is SuccessState<*> -> { psVm.getUserWallets(userState = userState) }
            else -> Unit
        }
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
                    WalletSearchView(psVm.textState) { updatedState ->
                        psVm.textState = updatedState
                    }
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        floatingActionButton = { GenerateWalletButtonCompose(mContext, navController) },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        scaffoldState = scaffoldState

        ) { bodyPadding ->

        Column(modifier = Modifier.padding(bodyPadding)) {
            AnimatedVisibility(visible = psVm.currentWalletScreenState is LoadingState) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                val searchedText = psVm.textState.text
                psVm.filteredWalletListMap = if (searchedText.isEmpty()) {
                    psVm.walletListMap
                } else {
                    psVm.walletListMap.filter { it.blockchainType?.contains(searchedText, true) == true}
                }
                items(psVm.filteredWalletListMap) { task ->
                    WalletListItem(
                        label = task.blockchainType + " " + task.address,
                        onItemClick = { selectedWallet ->
                            //nesVm.updateCurrentProjectName(selectedWallet)
                            Toast.makeText(mContext, task.address, Toast.LENGTH_SHORT).show()
                        })
                }

            }
        }
        HarvestDialog(praxisCommand = psVm.walletScreenNavigationCommands, onConfirm = {
            psVm.walletScreenNavigationCommands = null
        })
    }
}

@Composable
fun GenerateWalletButtonCompose(context: Context, navController: NavHostController){

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
        //onClick = { navController.navigate(HarvestRoutes.Screen.ADD_ACTION) }
        onClick = { Toast.makeText(context, "This will generate wallet", Toast.LENGTH_SHORT).show()},
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}