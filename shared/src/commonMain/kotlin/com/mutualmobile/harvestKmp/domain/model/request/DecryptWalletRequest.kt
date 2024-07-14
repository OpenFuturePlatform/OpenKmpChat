package com.mutualmobile.harvestKmp.domain.model.request

data class DecryptWalletRequest(
    var encryptedText: String,
    var password: String
)
