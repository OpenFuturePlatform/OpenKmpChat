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
fun GroupProfileScreen(
    navController: NavHostController,
    cpVm: ContactProfileViewModel = get(),
    profileId: String?,
    isGroup: String?,
    userState: PraxisDataModel.DataState
) {
    println("GROUP PROFILE SCREEN with $profileId and $isGroup")
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
            is PraxisDataModel.SuccessState<*> -> { cpVm.getGroupDetails(groupId = profileId!!, userState) }
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
                    GroupNavigation(
                        isGroup = isGroup == "true",
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
                            GroupDetailBody(viewModel = cpVm, isGroup = isGroup == "true", navController = navController)
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
fun GroupNavigation(
    isGroup: Boolean,
    recipient: String?
) {
    Row(modifier = Modifier.fillMaxSize()
    )  {
        if (isGroup) {
            DefaultGroupPicture(displayName = "$recipient")
        } else {
            DefaultProfilePicture(displayName = "$recipient")
        }
        Text(
            text = "$recipient",
            modifier = Modifier.padding(start = 10.dp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun  GroupDetailBody(viewModel: ContactProfileViewModel, isGroup: Boolean, navController: NavHostController) {
    val participants = viewModel.participants
    val groupId = viewModel.currentGroupId
    val currentUser = viewModel.currentUser.collectAsState()

    println("PARTICIPANTS : $participants  and Current User: ${currentUser.value.email}")

    Column(
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.padding(top = 15.dp)) {
            if (isGroup) {
                DefaultGroupPicture(displayName = viewModel.currentProfileName)
            } else {
                DefaultProfilePicture(displayName = viewModel.currentProfileName)
            }
        }
        Column(modifier = Modifier) {
            Text(
                text = viewModel.currentProfileName,
                style = Typography.subtitle2,
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(bottom = 5.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Text(
                text = "Group: ${viewModel.participants.size} participants",
                style = Typography.subtitle2,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 100.dp),
    ) {
        AddMemberCard(navController, viewModel, groupId)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 160.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (participants.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 4.dp, end = 4.dp),
                //verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(participants) { _, contact ->

                    ParticipantCard(
                        cpVm = viewModel,
                        otherUser = contact,
                        groupId = groupId,
                        currentUser = currentUser.value,
                        onChatClicked = viewModel::onMemberClicked
                    )

                    Divider()
                }
                item {
                    Box(Modifier.height(70.dp))
                }
            }
        } else {
            Text(
                text = "NO USERS",
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun ParticipantCard(
    cpVm: ContactProfileViewModel,
    otherUser: String,
    currentUser: User,
    groupId: String,
    onChatClicked: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            //.padding(all = 4.dp)
            .clickable {
                onChatClicked.invoke(otherUser)
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
            ) {

                DefaultProfilePicture(displayName = otherUser)
                val suffix = if (otherUser == currentUser.email) "You - " else ""
                Text(
                    text = suffix + otherUser,
                    style = Typography.subtitle2,
                    modifier = Modifier.padding(start = 16.dp)
                )
                Icon(Icons.Default.Delete, contentDescription = null,
                    modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        .clickable {
                            cpVm.isDeleteDialogVisible = true
                            cpVm.deleteMemberId = otherUser
                            cpVm.deleteMemberGroupId = groupId
                    })

            }

        }
    }
}

@Composable
fun AddMemberCard(
    navController: NavHostController,
    viewModel: ContactProfileViewModel,
    groupId: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp)
            .clickable {
            }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    .clickable {
                        navController.navigate(HarvestRoutes.Screen.ADD_MEMBER.withGroup(groupId = groupId))
                    }
            ) {

                Icon(Icons.Default.Add, contentDescription = null)
                Text(
                    text = "Add new member",
                    style = Typography.subtitle2,
                    modifier = Modifier.padding(start = 16.dp)
                )

            }

        }
    }
}