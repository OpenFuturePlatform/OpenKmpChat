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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.utils.getIconUrl
import com.mutualmobile.harvestKmp.android.viewmodels.WalletsScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import java.math.RoundingMode

@Composable
fun ExpandableListItem(
    blockchainType: String,
    wallets: List<WalletResponse>,
    isExpanded: Boolean,
    onClick: () -> Unit,
    wsVm: WalletsScreenViewModel
) {
    var rate = "0"
    if (wsVm.exchangeRates.isNotEmpty()) {
        val coinGateRate = wsVm.exchangeRates[0]
        rate = when (blockchainType) {
            "BNB" -> coinGateRate.bnb.usd
            "BTC" -> coinGateRate.btc.usd
            "ETH" -> coinGateRate.eth.usd
            "TRX" -> coinGateRate.trx.usd
            "SOL" -> coinGateRate.sol.usd
            else -> coinGateRate.usdt.usd //usdt
        }
    }
    val iconUrl = getIconUrl(blockchainType)
    var totalBalance = 0.0
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
                contentScale = ContentScale.FillBounds,
                painter = iconUrl,
                contentDescription = "Icon"
            )
            Column(
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Text(blockchainType)
                Text("$rate $")
            }
        }

        Row {
            Column {
                val icon = if (isExpanded)
                    Icons.Filled.KeyboardArrowUp
                else
                    Icons.Filled.KeyboardArrowDown
                Icon(icon, contentDescription = "Expand")
            }
        }
    }
    wallets.forEach {
        AnimatedVisibility(visible = isExpanded) {

            val walletKey = it.address + it.blockchainType

            val amount = if (wsVm.walletBalances.isNotEmpty() && wsVm.walletBalances.containsKey(walletKey)) {
                wsVm.walletBalances[walletKey]
            } else {
                "0.00"
            }
            if (amount != null) {
                totalBalance = totalBalance.plus(amount.toDouble().times( rate.toDouble()))
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = {
                            //wsVm.isWalletDetailDialogVisible = true
                            //wsVm.currentWalletAddress = it.address!!
                            //wsVm.currentWalletPrivateKey = it.privateKey!!
                            wsVm.onWalletClicked(address = it.address!!, encryptedPrivetKey = it.privateKey!!, blockchainType = it.blockchainType!!)

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