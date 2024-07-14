package com.mutualmobile.harvestKmp.data.local

import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.squareup.sqldelight.db.SqlDriver
import db.Open_wallet

interface WalletLocal {
    var driver: SqlDriver?
    fun saveWallet(input: Wallet)
    fun getAll(): List<Open_wallet>
    fun getWallet(): Open_wallet?
    fun clear()
}