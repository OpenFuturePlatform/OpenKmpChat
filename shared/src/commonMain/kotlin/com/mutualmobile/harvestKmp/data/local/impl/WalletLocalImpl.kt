package com.mutualmobile.harvestKmp.data.local.impl

import com.mutualmobile.harvestKmp.data.local.WalletLocal
import com.mutualmobile.harvestKmp.db.BaseIoDB
import com.mutualmobile.harvestKmp.domain.model.Wallet
import com.squareup.sqldelight.db.SqlDriver
import db.Open_wallet

class WalletLocalImpl(override var driver: SqlDriver? = null) : WalletLocal {

    private val database by lazy { BaseIoDB(driver!!) }
    private val dbQuery by lazy { database.walletDBQueries }
    override fun saveWallet(input: Wallet) {
        println("Save Wallet locally: $input")
        dbQuery.insertWallet(
            uid = input.id.toString(),
            userId = input.userId,
            address = input.address,
            privateKey = input.privateKey,
            seedPhrase = input.seedPhrase,
            time = input.seconds,
            blockchainType = input.blockchainType.name
        )
    }

    override fun getAll(): List<Open_wallet> {
        return dbQuery.selectAllWallets().executeAsList()
     }

    override fun getWallet(): Open_wallet? {
        return dbQuery.selectAllWallets().executeAsOneOrNull()
    }

    override fun clear() {
        dbQuery.deleteAllWallets()
    }
}