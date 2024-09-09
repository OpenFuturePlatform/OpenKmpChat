package com.mutualmobile.harvestKmp.android.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.di.AuthApiUseCaseComponent
import com.mutualmobile.harvestKmp.domain.model.request.OpenOrganization
import com.mutualmobile.harvestKmp.domain.model.response.ApiResponse
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.GetUserDataModel
import com.mutualmobile.harvestKmp.features.datamodels.orgApiDataModels.FindOrgByIdDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class MainActivityViewModel : ViewModel() {
    private val getUserDataModel = GetUserDataModel()
    private val findOrgByIdDataModel = FindOrgByIdDataModel()

    var getUserState: PraxisDataModel.DataState by mutableStateOf(PraxisDataModel.EmptyState)
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
                if (newUserState is PraxisDataModel.SuccessState<*>) {
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

    private fun fetchUserOrganization() {
        with(findOrgByIdDataModel) {
            dataFlow.onEach { newOrgState ->
                if (newOrgState is PraxisDataModel.SuccessState<*>) {
                    userOrganization = (newOrgState.data as? ApiResponse<OpenOrganization>)?.data
                }
            }.launchIn(viewModelScope)
            user?.orgId?.let { nnOrgId ->
                findOrgById(orgId = nnOrgId)
            }
            activate()
        }
    }
}