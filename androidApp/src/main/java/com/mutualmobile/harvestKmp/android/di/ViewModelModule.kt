package com.mutualmobile.harvestKmp.android.di

import com.mutualmobile.harvestKmp.android.viewmodels.*
import org.koin.dsl.module

val viewModelModule = module {
    single { NewEntryScreenViewModel() }
    single { MainActivityViewModel() }
    single { LandingScreenViewModel() }
    single { TimeScreenViewModel() }
    single { FindWorkspaceViewModel() }
    single { LoginViewModel() }
    single { PinInputViewModel() }
    single { ChangePasswordViewModel() }
    single { ForgotPasswordViewModel() }
    single { ProjectScreenViewModel() }
    single { ExistingOrgSignUpScreenViewModel() }
    single { NewOrgSignUpScreenViewModel() }
    single { WorkTypeScreenViewModel() }
    single { UserHomeViewModel() }
    single { ChatRoomScreenViewModel() }
    single { ChatViewModel(get()) }
    single { ChatPrivateScreenViewModel() }
    single { UserListViewModel()}
    single { AddGroupScreenViewModel()}
    single { ContactProfileViewModel() }
    single { TaskScreenViewModel() }
    single { WalletDetailScreenViewModel() }
    single { WalletsScreenViewModel() }
    single { WalletSenderDetailScreenViewModel() }
    single { WalletReceiverDetailScreenViewModel() }
    single { BiometricViewModel()}
}