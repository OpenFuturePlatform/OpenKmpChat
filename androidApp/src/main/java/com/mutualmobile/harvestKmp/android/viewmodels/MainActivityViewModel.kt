package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.request.OpenOrganization
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.GetUserDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainActivityViewModel : ViewModel() {
    private val getUserDataModel = GetUserDataModel()

    var getUserState: OpenDataModel.DataState by mutableStateOf(OpenDataModel.EmptyState)
        private set
    var user: GetUserResponse? by mutableStateOf(null)
    var userOrganization: OpenOrganization? by mutableStateOf(null)

    val doesLocalUserExist: Boolean = getUserDataModel.getLocalUser() != null

    init {
        println("Called MainActivityViewModel init")
        fetchUser()
    }

    fun fetchUser() {
        with(getUserDataModel) {
            dataFlow.onEach { newUserState ->
                getUserState = newUserState
                if (newUserState is OpenDataModel.SuccessState<*>) {
                    user = newUserState.data as? GetUserResponse

//                    FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
//                        println("FcmToken: $fcmToken")
//                        getUserDataModel.saveFcmToken(fcmToken)
//                    }

                }
            }.launchIn(viewModelScope)
            activate()
        }
    }
}