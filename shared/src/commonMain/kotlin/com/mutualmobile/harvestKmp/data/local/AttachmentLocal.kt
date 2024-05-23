package com.mutualmobile.harvestKmp.data.local

import com.mutualmobile.harvestKmp.domain.model.Attachment
import com.squareup.sqldelight.db.SqlDriver
import db.Open_attachment

interface AttachmentLocal {
    var driver: SqlDriver?
    fun saveAttachment(input: Attachment)
    fun getAll(): List<Open_attachment>
    fun findByHash(hash: String): Open_attachment?
    fun clear()
}