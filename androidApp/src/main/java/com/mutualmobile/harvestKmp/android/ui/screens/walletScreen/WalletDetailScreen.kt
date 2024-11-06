package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen

import android.R
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.WalletScreenDetailDialog
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.getIconUrl
import com.mutualmobile.harvestKmp.android.viewmodels.WalletDetailScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.response.ExchangeRate
import org.koin.androidx.compose.get
import java.math.BigDecimal
import java.math.RoundingMode

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WalletDetailScreen(
    navController: NavHostController,
    userState: OpenDataModel.DataState,
    wdsVm: WalletDetailScreenViewModel = get(),
    address: String?,
    privateKey: String?,
    blockchainType: String?
) {

    val trx = wdsVm.walletTransactions.size
    val scaffoldState = rememberScaffoldState()
    val mContext = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        modifier = Modifier
                            .size(PROFILE_PICTURE_SIZE.dp)
                            .clip(CircleShape)
                            .padding(PaddingValues(8.dp, 8.dp)),
                        contentScale = ContentScale.Crop,
                        painter = getIconUrl(blockchainType!!),
                        contentDescription = "Icon"
                    )
                    Text(
                        text = address?.take(6) + "..." + address?.takeLast(6),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(HarvestRoutes.Screen.USER_WALLETS) }) {
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
            modifier = Modifier.fillMaxSize().padding(bodyPadding)
                .then(Modifier.padding(top = 20.dp)),
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = getIconUrl(blockchainType!!),
                contentDescription = "Icon"
            )
            Text(
                text = wdsVm.walletBalance.toString() + " " + blockchainType.uppercase(),
                fontSize = 20.sp,
            )
            for (i in 0 until wdsVm.exchangeRate.size) {
                val totalBalanceUsd =
                    wdsVm.walletBalance.toBigDecimal().multiply(wdsVm.exchangeRate[i].price.toBigDecimal())
                        .setScale(2, RoundingMode.DOWN)
                Text(text = "≈ $totalBalanceUsd $", fontSize = 18.sp)
            }

            CircleButtonsWithTitles(mContext, wdsVm)

            Transactions(wdsVm)
        }

    }

    ShowDialogs(wdsVm)

    LaunchedEffect(wdsVm.currentNavigationCommand) {
        when (wdsVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (wdsVm.currentNavigationCommand as NavigationOpenCommand).screen
                println("WalletDetailScreen Launcher: $destination")
                wdsVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }

            }
        }
    }

    LaunchedEffect(Unit) {
        when (userState) {
            is OpenDataModel.SuccessState<*> -> {
                wdsVm.getWalletDetail(address!!, privateKey!!, blockchainType!!)
            }

            else -> Unit
        }
    }

}

@Composable
fun ShowDialogs(wsVm: WalletDetailScreenViewModel) {

    if (wsVm.isWalletDetailDialogVisible) {
        WalletScreenDetailDialog(
            onDismiss = {
                wsVm.isWalletDetailDialogVisible = false
                wsVm.isWalletDecryptDialogVisible = false
                wsVm.password = ""
            },
            wsVm = wsVm
        )
    }


}

@Composable
fun CircleButtonsWithTitles(mContext: Context, wsVm: WalletDetailScreenViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable {
                //wsVm.isWalletDetailDialogVisible = true
                wsVm.onReceiverClicked(
                    address = wsVm.address,
                    encryptedPrivetKey = wsVm.privateKey,
                    blockchainType = wsVm.blockchainType
                )
            }
        ) {
            // Circle button 1
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xffcacccc), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "content description",
                    tint = Color(0xff092929),
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Send",
                fontSize = 16.sp,
                //fontWeight = FontWeight(700),
                //color = Color(0xff000000),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.clickable {
                //wsVm.isWalletDetailDialogVisible = true
                wsVm.onSenderClicked(
                    address = wsVm.address,
                    encryptedPrivetKey = wsVm.privateKey,
                    blockchainType = wsVm.blockchainType
                )

            }
        ) {
            // Circle button 2
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xffcacccc), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "content description",
                    tint = Color(0xff092929),
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Receive",
                fontSize = 16.sp,
                //fontWeight = FontWeight(700),
                //color = Color(0xff000000),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Transactions(viewModel: WalletDetailScreenViewModel) {
    val transactions = viewModel.walletTransactions
    val exchangeRates = viewModel.exchangeRate
    if (transactions.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(transactions) { position, transaction ->
                TransactionCard(
                    position = position,
                    dateAt = transaction.date.toString(),
                    currentAddress = viewModel.address,
                    fromAddress = transaction.from.last(),
                    toAddress = transaction.to,
                    amount = transaction.amount.toString(),
                    blockchainType = viewModel.blockchainType,
                    rates = exchangeRates
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                contentScale = ContentScale.FillHeight,
                painter = painterResource(MR.images.search.drawableResId),
                contentDescription = "Icon"
            )
        }
    }
}

@Composable
fun TransactionCard(
    position: Int,
    dateAt: String,
    blockchainType: String,
    currentAddress: String,
    fromAddress: String,
    toAddress: String,
    amount: String,
    rates: List<ExchangeRate>
) {
    val isOutGoing = currentAddress == fromAddress
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable { }
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
//            Box(
//                modifier = Modifier
//                    .size(50.dp)
//                    .padding(all = 10.dp)
//                    .background(Color.Green, CircleShape),
//                contentAlignment = Alignment.Center,
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.btn_dropdown),
//                    contentDescription = "Income",
//                    modifier = Modifier.size(24.dp)
//                )
//            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Transfer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                val sDetail = if (isOutGoing) "To: ${toAddress.take(6) + "..." + toAddress.takeLast(6)}" else "From: ${fromAddress.take(6) + "..." + fromAddress.takeLast(6)}"
                Text(
                    text = sDetail,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(1f)
            ) {
                var amountUsd = BigDecimal.ZERO
                for (i in 0 until rates.size) {
                    amountUsd =
                        amount.toBigDecimal().multiply(rates[i].price.toBigDecimal())
                            .setScale(2, RoundingMode.DOWN)
                }
                val amountSign = if (isOutGoing) "-" else "+"
                Text(
                    text = "$amountSign $amount  $blockchainType",
                    color = if (isOutGoing) Color(0xffe74c3c) else Color(0xff18c331),
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "≈ $amountUsd $ ",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

    }
}