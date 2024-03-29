package com.mutualmobile.harvestKmp.data.local

import com.mutualmobile.harvestKmp.db.BaseIoDB
import com.mutualmobile.harvestKmp.db.asFlow
import com.mutualmobile.harvestKmp.db.mapToList
import com.mutualmobile.harvestKmp.domain.model.Message
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import db.Harvest_chat
import kotlinx.coroutines.flow.Flow

class ChatLocalImpl(override var driver: SqlDriver? = null) : ChatLocal {

    private val database by lazy { BaseIoDB(driver!!) }
    private val dbQuery by lazy { database.chatDBQueries }
    override fun saveChat(input: Message) {
        dbQuery.insertChat(
            uid = input.id.toString(),
            userId = input.user.id,
            content = input.text,
            time = input.seconds
        )
    }

    override fun getAll(): List<Harvest_chat> {
        return dbQuery.selectAllChats().executeAsList()
    }

    override fun getChat(): Harvest_chat? {
        return dbQuery.selectAllChats().executeAsOneOrNull()
    }

    override fun clear() {
        dbQuery.deleteAllChats()
    }
}