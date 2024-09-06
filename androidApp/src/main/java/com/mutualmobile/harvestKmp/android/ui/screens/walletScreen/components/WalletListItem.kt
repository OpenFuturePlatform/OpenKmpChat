package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import java.math.RoundingMode

@Composable
fun WalletListItem(
    blokchainType: String,
    address: String,
    balance: String,
    privateKey: String,
    onItemClick: (String) -> Unit,
    wsVm: WalletScreenViewModel
) {
    var rate = "0"
    if (wsVm.exchangeRates.isNotEmpty()) {
        val coinGateRate = wsVm.exchangeRates[0]
        rate = when (blokchainType) {
            "BNB" -> coinGateRate.bnb.usdt
            "BTC" -> coinGateRate.btc.usdt
            "ETH" -> coinGateRate.eth.usdt
            "TRX" -> coinGateRate.trx.usdt
            else -> "0"
        }
    }
    val iconUrl = when (blokchainType) {
        "BNB" -> painterResource(MR.images.bnb.drawableResId)
        "BTC" -> painterResource(MR.images.btc.drawableResId)
        "ETH" -> painterResource(MR.images.eth.drawableResId)
        "TRX" -> painterResource(MR.images.trx.drawableResId)
        else -> {painterResource(MR.images.bnb.drawableResId)}
    }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { onItemClick(privateKey) })
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
                    Text(blokchainType)
                    Text("$rate $")
                }
            }
            Column {
                Text(balance)
                val totalBalanceUsd = balance.toBigDecimal().multiply(rate.toBigDecimal()).setScale(2, RoundingMode.DOWN)
                Text("$totalBalanceUsd $")
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
        )
    }
}

