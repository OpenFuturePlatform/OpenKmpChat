package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import java.math.RoundingMode

@Composable
fun ExpandableListItem(
    blockchainType: String,
    wallets: List<WalletResponse>,
    isExpanded: Boolean,
    onClick: () -> Unit,
    wsVm: WalletScreenViewModel
) {
    var rate = "0"
    if (wsVm.exchangeRates.isNotEmpty()) {
        val coinGateRate = wsVm.exchangeRates[0]
        rate = when (blockchainType) {
            "BNB" -> coinGateRate.bnb.usdt
            "BTC" -> coinGateRate.btc.usdt
            "ETH" -> coinGateRate.eth.usdt
            "TRX" -> coinGateRate.trx.usdt
            "SOL" -> coinGateRate.sol.usdt
            else -> "1" //usdt
        }
    }
    val iconUrl = getIconUrl(blockchainType)

    Row(
        modifier = Modifier
            .clickable { onClick() }
            .background(Color.White)
            .fillMaxWidth()
            .padding(PaddingValues(8.dp, 8.dp)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Image(
                modifier = Modifier
                    .size(PROFILE_PICTURE_SIZE.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = iconUrl,
                contentDescription = "Icon"
            )
            Column {
                Text(blockchainType)
            }
        }
        Column {
            Text("$rate $")
        }
        Column{
            val icon = if (isExpanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            Icon(icon, contentDescription = "Expand")
        }
    }
    wallets.forEach {
        AnimatedVisibility(visible = isExpanded) {

            val walletKey = it.address + it.blockchainType
            val totalBalance = it.balance
            val amount = if (wsVm.walletBalances.isNotEmpty() && wsVm.walletBalances.containsKey(walletKey)) {
                wsVm.walletBalances[walletKey]
            } else {
                "0.00"
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = {
                            wsVm.isWalletDetailDialogVisible = true
                            wsVm.currentWalletAddress = it.address!!
                            wsVm.currentWalletPrivateKey = it.privateKey!!

                        })
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(PaddingValues(8.dp, 8.dp)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        if (blockchainType == "USDT") {
                            Image(
                                modifier = Modifier
                                    .size(PROFILE_PICTURE_SIZE.dp)
                                    .clip(CircleShape)
                                    .padding(PaddingValues(8.dp, 8.dp)),
                                contentScale = ContentScale.Crop,
                                painter = getIconUrl(it.blockchainType!!),
                                contentDescription = "Icon"
                            )
                        }
                        Column {
                            Text(it.address?.take(5)!! + "..." + it.address?.takeLast(2)!!)
                        }
                    }
                    Column {
                        Text(amount!!)
                        val totalBalanceUsd =
                            amount.toBigDecimal().multiply(rate.toBigDecimal()).setScale(2, RoundingMode.DOWN)
                        Text("$totalBalanceUsd $")
                    }
                }

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }

}

@Composable
fun getIconUrl(blokchainType: String): Painter {
    return when (blokchainType) {
        "BNB" -> painterResource(MR.images.bnb.drawableResId)
        "BTC" -> painterResource(MR.images.btc.drawableResId)
        "ETH" -> painterResource(MR.images.eth.drawableResId)
        "TRX" -> painterResource(MR.images.trx.drawableResId)
        "SOL" -> painterResource(MR.images.sol.drawableResId)
        "USDT" -> painterResource(MR.images.usdt.drawableResId)
        else -> {
            painterResource(MR.images.bnb.drawableResId)
        }
    }
}