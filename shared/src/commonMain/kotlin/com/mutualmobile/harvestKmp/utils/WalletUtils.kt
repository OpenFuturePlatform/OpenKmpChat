//package com.mutualmobile.harvestKmp.utils
//
//fun generateSeedCode(): String {
//    val seedCode = StringBuilder()
//    val wallet = HDWallet(seedPhrase, passphrase)
//    val entropy = ByteArray(Words.TWELVE.byteLength())
//    SecureRandom().nextBytes(entropy)
//    MnemonicGenerator(English.INSTANCE)
//        .createMnemonic(entropy, seedCode::append)
//
//    return seedCode.toString()
//}
//fun getDerivedKey(derivationPath: IntArray, seedCode: String): Bip32ECKeyPair {
//    // Generate a BIP32 master keypair from the mnemonic phrase
//    val masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(seedCode, ""))
//    // Derive the keypair using the derivation path
//    return Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath)
//}