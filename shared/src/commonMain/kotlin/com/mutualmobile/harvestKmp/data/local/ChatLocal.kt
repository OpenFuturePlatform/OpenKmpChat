package com.mutualmobile.harvestKmp.data.local

import com.mutualmobile.harvestKmp.domain.model.Message
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import db.Harvest_chat
import kotlinx.coroutines.flow.Flow

interface ChatLocal {
    var driver: SqlDriver?
    fun saveChat(input: Message)
    fun getAll(): List<Harvest_chat>
    fun getChat(): Harvest_chat?
    fun clear()
}