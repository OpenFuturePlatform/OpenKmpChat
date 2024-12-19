package com.mutualmobile.harvestKmp.android.ui.screens.settingsScreen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.HarvestDialog
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withWalletDetail
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.DataState
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.EmptyState
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel.LogoutInProgress
import com.mutualmobile.harvestKmp.features.datamodels.authApiDataModels.UserDashboardDataModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun AccountSection(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var currentOpenCommand: OpenCommand? by remember { mutableStateOf(null) }
    var userLogoutState: DataState by remember { mutableStateOf(EmptyState) }

    val userDashboardDataModel: UserDashboardDataModel by remember {
        mutableStateOf(
            UserDashboardDataModel().apply {
                this.dataFlow.onEach { newState ->
                    userLogoutState = newState
                }.launchIn(coroutineScope)
                praxisCommand.onEach { newCommand ->
                    currentOpenCommand = newCommand
                    when (newCommand) {
                        is NavigationOpenCommand -> {
                            if (newCommand.screen.isBlank()) {
                                navController clearBackStackAndNavigateTo HarvestRoutes.Screen.LOGIN
                            }
                        }
                    }
                }.launchIn(coroutineScope)
            }
        )
    }

    ColoredText(text = stringResource(MR.strings.account_section_title.resourceId))
    SettingsListItem(
        title = stringResource(MR.strings.account_section_wallet_title.resourceId),
        showTopDivider = true,
        onClick = {
            navController.navigate(HarvestRoutes.Screen.USER_WALLETS)
        }
    )
//    SettingsListItem(
//        title = stringResource(MR.strings.account_section_help_center_item_title.resourceId),
//        showTopDivider = true
//    )
//    SettingsListItem(title = stringResource(MR.strings.account_section_refer_friend_item_title.resourceId))
//    SettingsListItem(
//        title = stringResource(MR.strings.account_section_email_support_item_title.resourceId),
//        showTopDivider = true
//    )
//    SettingsListItem(
//        title = stringResource(MR.strings.account_section_help_center_item_title.resourceId),
//        showTopDivider = true
//    )
//    SettingsListItem(
//        title = stringResource(MR.strings.account_section_switch_accounts_item_title.resourceId),
//        showTopDivider = true
//    )
    SettingsListItem(
        title = stringResource(MR.strings.account_section_change_password_item_title.resourceId),
        showTopDivider = true,
        onClick = {
            navController.navigate(HarvestRoutes.Screen.CHANGE_PASSWORD)
        }
    )
    SettingsListItem(
        title = stringResource(MR.strings.account_section_signout_item_title.resourceId),
        showTopDivider = true,
        onClick = {
            userDashboardDataModel.logout()
        }
    )
    AnimatedVisibility(visible = userLogoutState is LogoutInProgress) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    HarvestDialog(openCommand = currentOpenCommand, onConfirm = {
        currentOpenCommand = null
    })
}