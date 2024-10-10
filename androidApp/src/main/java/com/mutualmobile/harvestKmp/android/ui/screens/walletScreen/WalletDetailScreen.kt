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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.GenerateWalletDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.HarvestDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.WalletDetailDialog
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.ExpandableListItem
import com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components.WalletSearchView
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.*
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get


@Composable
fun WalletDetailScreen(
    navController: NavHostController,
    userState: DataState,
    wsVm: WalletScreenViewModel = get(),
    address: String,
    privateKey: String
) {

    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current

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
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        scaffoldState = scaffoldState

    ) { bodyPadding ->

        Column(modifier = Modifier.padding(bodyPadding)) {
            AnimatedVisibility(visible = wsVm.currentWalletScreenState is LoadingState) {
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Text(
                text = stringResource(id = MR.strings.app_name.resourceId),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.h6
            )

        }
        HarvestDialog(praxisCommand = wsVm.walletScreenNavigationCommands, onConfirm = {
            wsVm.walletScreenNavigationCommands = null
        })
    }
}