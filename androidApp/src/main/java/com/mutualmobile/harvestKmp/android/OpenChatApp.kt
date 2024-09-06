package com.mutualmobile.harvestKmp.android

import android.app.Application
import com.mutualmobile.harvestKmp.android.di.viewModelModule
import com.mutualmobile.harvestKmp.db.DriverFactory
import com.mutualmobile.harvestKmp.di.SharedComponent
import com.mutualmobile.harvestKmp.di.initSharedDependencies
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext

val sharedComponent = SharedComponent()

class OpenChatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initSharedDependencies().apply {
            androidContext(this@OpenChatApp)
            modules(viewModelModule)
        }
        GlobalScope.launch {
            preCheckSqlite()
        }
    }

    private suspend fun preCheckSqlite() {
        if (sharedComponent.provideHarvestUserLocal().driver == null) {
            val driver = DriverFactory(context = this).createDriverBlocking()
            sharedComponent.provideHarvestUserLocal().driver = driver
        }
        if (sharedComponent.provideChatLocal().driver == null) {
            val driver = DriverFactory(context = this).createDriverBlocking()
            sharedComponent.provideChatLocal().driver = driver
        }
        if (sharedComponent.provideWalletLocal().driver == null) {
            val driver = DriverFactory(context = this).createDriverBlocking()
            sharedComponent.provideWalletLocal().driver = driver
        }
    }
}