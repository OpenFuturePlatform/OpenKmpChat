package com.mutualmobile.harvestKmp.data.local

import com.squareup.sqldelight.db.SqlDriver
import db.Firebase_token

interface TokenLocal {
    var driver: SqlDriver?
    fun saveToken(token: String)
    fun getAll(): List<Firebase_token>
    fun get(): Firebase_token?
    fun clear()
}