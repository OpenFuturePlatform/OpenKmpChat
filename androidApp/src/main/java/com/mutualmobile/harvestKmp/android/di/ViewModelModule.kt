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
    single { ChangePasswordViewModel() }
    single { ForgotPasswordViewModel() }
    single { ProjectScreenViewModel() }
    single { ExistingOrgSignUpScreenViewModel() }
    single { NewOrgSignUpScreenViewModel() }
    single { WorkTypeScreenViewModel() }
    single { UserHomeViewModel() }
    single { ChatRoomViewModel() }
    single { ChatViewModel(get()) }
    single { ChatPrivateViewModel(get()) }
    single { UserListViewModel()}
    single { AddGroupViewModel()}
    single { ContactProfileViewModel() }
}