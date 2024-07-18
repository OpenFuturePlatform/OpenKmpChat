object CommonDependencyVersions {
    const val multiplatformSettings = "0.9"
    const val sqlDelight = "1.5.3"
    const val ktor = "2.0.1"
    const val kotlinxDateTime = "0.3.2"
    const val kotlinxSerialization = "1.3.2"
    const val coroutines = "1.6.1"
    const val koin = "3.1.4"
    const val junit = "4.13.2"
    const val mokoResources = AppDependencyVersions.mokoResources
    const val gemini = "0.1.2"
}

object CommonMainDependencies {
    val implementation = listOf(
        "com.russhwolf:multiplatform-settings-no-arg:${CommonDependencyVersions.multiplatformSettings}",
        "com.russhwolf:multiplatform-settings:${CommonDependencyVersions.multiplatformSettings}",
        "com.squareup.sqldelight:runtime:${CommonDependencyVersions.sqlDelight}",
        "io.ktor:ktor-client-core:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-client-json:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-client-auth:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-client-content-negotiation:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-serialization-kotlinx-json:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-client-websockets:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-client-logging:${CommonDependencyVersions.ktor}",
        "io.ktor:ktor-client-serialization:${CommonDependencyVersions.ktor}",
        "org.jetbrains.kotlinx:kotlinx-datetime:${CommonDependencyVersions.kotlinxDateTime}",
        "org.jetbrains.kotlinx:kotlinx-serialization-core:${CommonDependencyVersions.kotlinxSerialization}",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${CommonDependencyVersions.coroutines}",
        "com.google.code.gson:gson:2.10.1",
//        "org.web3j:core:5.0.0",
//        "org.bitcoinj:bitcoinj-core:0.16.1",
//        "io.github.novacrypto:BIP39:2018.10.06",
    )

    val api = listOf(
        "io.insert-koin:koin-core:${CommonDependencyVersions.koin}",
        "dev.icerock.moko:resources:${CommonDependencyVersions.mokoResources}",
        // ChatGPT SDK
        //"co.yml:ychat:1.4.1",
        // GenAI SDK
        //"com.google.ai.client.generativeai:generativeai:${CommonDependencyVersions.gemini}",
    )
}

object CommonTestDependencies {
    val implementation = listOf(
        "com.russhwolf:multiplatform-settings-test:${CommonDependencyVersions.multiplatformSettings}",
    )
    val kotlin = listOf(
        "test-common",
        "test-annotations-common"
    )
}

object CommonPlugins {
    val plugins = listOf(
        "com.android.library",
        "kotlinx-serialization",
        "com.squareup.sqldelight",
        "com.rickclephas.kmp.nativecoroutines",
        "dev.icerock.mobile.multiplatform-resources",
    )
    val kotlinPlugins = listOf(
        "multiplatform",
        "native.cocoapods"
    )
}
