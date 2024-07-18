package com.mutualmobile.harvestKmp.android.ui.screens.walletScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel

@Composable
fun WalletListItem(
    blokchainType: String,
    address: String,
    privateKey: String,
    onItemClick: (String) -> Unit,
    wsVm: WalletScreenViewModel
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = { onItemClick(privateKey) })
                .background(Color.White)
                //.height(57.dp)
                .fillMaxWidth()
                .padding(PaddingValues(8.dp, 8.dp))
        ) {
            Text(text = blokchainType, fontSize = 18.sp, color = Color.Black, modifier = Modifier.padding(all = 8.dp))
            Text(text = address.substring(0, 25).plus("..."), fontSize = 18.sp, color = Color.Black, modifier = Modifier.padding(all = 8.dp))
            IconButton(
                modifier = Modifier.padding(end = 1.dp),
                onClick = {
                        wsVm.currentWalletPrivateKey = privateKey
                        wsVm.currentWalletAddress = address
                        wsVm.isWalletDetailDialogVisible = true
                }) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null)
            }
        }
    }
}

