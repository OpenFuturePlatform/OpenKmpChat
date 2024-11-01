package com.mutualmobile.harvestKmp.android.ui.screens.userScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.CommonAlertDialog
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.ContactProfileViewModel
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import org.koin.androidx.compose.get


@Composable
fun ContactProfileScreen(
    navController: NavHostController,
    cpVm: ContactProfileViewModel = get(),
    profileId: String?,
    userState: OpenDataModel.DataState
) {
    println("CONTACT PROFILE SCREEN with $profileId")
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(cpVm.currentNavigationCommand) {
        when (cpVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (cpVm.currentNavigationCommand as NavigationOpenCommand).screen
                cpVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is OpenDataModel.SuccessState<*> -> { cpVm.getUserDetails(userId = profileId!!, userState) }
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