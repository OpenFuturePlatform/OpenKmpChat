package com.mutualmobile.harvestKmp.data.local.impl

import com.mutualmobile.harvestKmp.data.local.TokenLocal
import com.mutualmobile.harvestKmp.db.BaseIoDB
import com.squareup.sqldelight.db.SqlDriver
import db.Firebase_token
import kotlinx.datetime.Clock

class TokenLocalImpl(override var driver: SqlDriver? = null) : TokenLocal {

    private val database by lazy { BaseIoDB(driver!!) }
    private val dbQuery by lazy { database.tokenDBQueries }

    override fun saveToken(token: String) {
        dbQuery.insertToken(token = token, time = Clock.System.now().toEpochMilliseconds())
    }

    override fun getAll(): List<Firebase_token> {
        return dbQuery.selectAllTokens().executeAsList()
    }

    override fun get(): Firebase_token? {
        return dbQuery.selectAllTokens().executeAsOneOrNull()
    }

    override fun clear() {
        dbQuery.deleteAllTokens()
    }
}