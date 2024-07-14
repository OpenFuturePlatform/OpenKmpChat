package com.mutualmobile.harvestKmp.android.ui.screens.userScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.CommonAlertDialog
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultGroupPicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.theme.Typography
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.ContactProfileViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes.Screen.withGroup
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.request.User
import org.koin.androidx.compose.get


@Composable
fun ContactProfileScreen(
    navController: NavHostController,
    cpVm: ContactProfileViewModel = get(),
    profileId: String?,
    userState: PraxisDataModel.DataState
) {
    println("CONTACT PROFILE SCREEN with $profileId")
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(cpVm.currentNavigationCommand) {
        when (cpVm.currentNavigationCommand) {
            is NavigationPraxisCommand -> {
                val destination = (cpVm.currentNavigationCommand as NavigationPraxisCommand).screen
                cpVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is PraxisDataModel.SuccessState<*> -> { cpVm.getUserDetails(userId = profileId!!, userState) }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                title = {
                    ProfileNavigation(
                        recipient = cpVm.currentProfileName
                    )
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        scaffoldState = scaffoldState
    ) {
        OpenChatTheme {
            Surface {
                Box(modifier = Modifier.fillMaxSize().then(Modifier.padding(it))) {
                    Image(painterResource(
                        MR.images.background.drawableResId),
                        null,
                        contentScale = ContentScale.FillHeight)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.weight(1f)) {
                            ProfileDetailBody(viewModel = cpVm, navController = navController)
                        }
                    }

                }
            }
        }
    }

    if (cpVm.isDeleteDialogVisible) {
        println("REMOVE MEMBER ${cpVm.deleteMemberId} from GROUP ${cpVm.deleteMemberGroupId}")
        CommonAlertDialog(
            onDismiss = {
                cpVm.isDeleteDialogVisible = false
                cpVm.deleteMemberId = ""
                cpVm.deleteMemberGroupId = ""
            },
            onConfirm = {
                cpVm.removeMemberClicked()
            },
            titleProvider = { MR.strings.delete_work_dialog_title.get() },
            bodyTextProvider = { MR.strings.remove_member_dialog_bodyText.get() }
        )
    }
}

@Composable
fun ProfileNavigation(
    recipient: String?
) {
    Row(modifier = Modifier.fillMaxSize()
    )  {

        DefaultProfilePicture(displayName = "$recipient")

        Text(
            text = "$recipient",
            modifier = Modifier.padding(start = 10.dp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun  ProfileDetailBody(viewModel: ContactProfileViewModel, navController: NavHostController) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.padding(top = 15.dp)) {
            DefaultProfilePicture(displayName = viewModel.currentProfileName)
        }
    }
}