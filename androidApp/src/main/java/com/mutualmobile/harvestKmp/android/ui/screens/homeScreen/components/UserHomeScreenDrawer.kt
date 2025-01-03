package com.mutualmobile.harvestKmp.android.ui.screens.homeScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.DataState
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.SuccessState
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse

@Composable
fun UserHomeScreenDrawer(
    currentDrawerScreen: UserHomeScreenDrawerItemType,
    closeDrawer: () -> Unit,
    onScreenChanged: (UserHomeScreenDrawerItemType) -> Unit,
    goToSettingsScreen: () -> Unit,
    userState: DataState
) {
    UserInfoSection(
        userName = (userState as? SuccessState<GetUserResponse>)?.data?.firstName,
        lastName = (userState as? SuccessState<GetUserResponse>)?.data?.lastName
    )

    UserHomeScreenDrawerItemType.values().forEach { drawerItem ->
        UserHomeScreenDrawerItem(
            itemType = drawerItem,
            isSelected = currentDrawerScreen == drawerItem
        ) {
            onScreenChanged(drawerItem)
            closeDrawer()
        }
    }

    Divider(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .padding(horizontal = 8.dp),
        color = Color.White.copy(alpha = 0.075f),
        thickness = 0.6.dp
    )

    SettingsButton(goToSettingsScreen = goToSettingsScreen)
}

@Composable
private fun SettingsButton(goToSettingsScreen: () -> Unit) {
    Text(
        text = stringResource(id = MR.strings.drawer_settings_btn_txt.resourceId),
        style = MaterialTheme.typography.subtitle2.copy(
            color = MaterialTheme.colors.surface.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { goToSettingsScreen() }
            .padding(16.dp),
    )
}
