package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.PraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.request.CreateWalletRequest
import com.mutualmobile.harvestKmp.domain.model.request.DecryptWalletRequest
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
import com.mutualmobile.harvestKmp.features.datamodels.userWalletDataModels.GetUserWalletsDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WalletScreenViewModel : ViewModel() {
    var walletListMap by mutableStateOf(emptyList<WalletResponse>())

    var filteredWalletListMap: List<WalletResponse> = emptyList()

    var textState by mutableStateOf(TextFieldValue(""))

    var currentWalletScreenState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)
    var walletScreenNavigationCommands: PraxisCommand? by mutableStateOf(null)

    private val getUserWalletsDataModel = GetUserWalletsDataModel()

    init {
        with(getUserWalletsDataModel) {
            observeDataState()
            observeNavigationCommands()
        }
    }

    private fun GetUserWalletsDataModel.observeNavigationCommands() {
        praxisCommand.onEach { newCommand ->
            walletScreenNavigationCommands = newCommand
        }.launchIn(viewModelScope)
    }

    private fun GetUserWalletsDataModel.observeDataState() {
        dataFlow.onEach { walletState ->
            currentWalletScreenState = walletState
            when (walletState) {
                is PraxisDataModel.SuccessState<*> -> {
                    println("WalletState $walletState")
                    walletListMap =
                        walletState.data as List<WalletResponse>
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun getUserWallets(userState: PraxisDataModel.SuccessState<*>) {
        getUserWalletsDataModel.getUserWallets(
            userId = (userState.data as GetUserResponse).email ?: ""
        )
    }

    fun generateWallet(createWalletRequest: CreateWalletRequest) {
        getUserWalletsDataModel.generateWallets(createWalletRequest)
    }

    fun decryptWallet(decryptWalletRequest: DecryptWalletRequest) {
        getUserWalletsDataModel.decryptWallet(decryptWalletRequest)
    }

    fun resetAll(onComplete: () -> Unit = {}) {
        walletListMap = emptyList()
        filteredWalletListMap = emptyList()
        textState = TextFieldValue("")
        onComplete()
    }
}