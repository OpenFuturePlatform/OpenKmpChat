package com.mutualmobile.harvestKmp.data.local.impl

import com.mutualmobile.harvestKmp.data.local.AttachmentLocal
import com.mutualmobile.harvestKmp.db.BaseIoDB
import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.squareup.sqldelight.db.SqlDriver
import db.Open_attachment

class AttachmentLocalImpl(override var driver: SqlDriver? = null) : AttachmentLocal {

    private val database by lazy { BaseIoDB(driver!!) }
    private val dbQuery by lazy { database.attachmentDBQueries }

    override fun saveAttachment(input: Attachment) {
        dbQuery.insertAttachment(
            uid = input.id.toString(),
            attachmentUrl = input.attachmentUrl,
            captionText = input.captionText,
            fileType = input.fileType,
            fileName = input.fileName,
            fileCheckSum = input.fileCheckSum,
            isSent = if (input.isSent) 1 else 0,
            time = input.seconds,
        )
    }

    override fun getAll(): List<Open_attachment> {
        return dbQuery.selectAllAttachments().executeAsList()
     }

    override fun findByHash(hash: String): Open_attachment? {
        return dbQuery.selectAttachmentByChecksum(hash).executeAsOneOrNull()
    }

    override fun clear() {
        dbQuery.deleteAllAttachments()
    }
}