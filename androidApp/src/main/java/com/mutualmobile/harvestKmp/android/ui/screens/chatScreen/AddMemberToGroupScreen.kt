package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.LoadingIndicator
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.AddGroupScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.UserListViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import org.koin.androidx.compose.get


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddMemberToGroupScreen(
    navController: NavHostController,
    ulVm: UserListViewModel = get(),
    agVm: AddGroupScreenViewModel = get(),
    groupId: String?,
    userState: OpenDataModel.DataState
) {

    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()

    println("ADD MEMBER TO GROUP SCREEN with GROUP ID: $groupId and participants: ${agVm.participants}")

    LaunchedEffect(ulVm.currentNavigationCommand) {
        when (ulVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (ulVm.currentNavigationCommand as NavigationOpenCommand).screen
                ulVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is OpenDataModel.SuccessState<*> -> {
                ulVm.getUserContacts(userState)
            }
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
                title = { Text("Add Member To Group") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        },
        floatingActionButton = { AddMemberToGroupButtonCompose(agVm, groupId!!) },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        scaffoldState = scaffoldState
    ) {
        OpenChatTheme {
            Surface {
                Box(modifier = Modifier.fillMaxSize().then(Modifier.padding(it))) {
                    Image(painterResource(
                        MR.images.background.drawableResId),
                        null,
                        contentScale = ContentScale.FillHeight
                    )

                    LoadingIndicator(loadingFlow = ulVm.loading)

                    //Divider(thickness = 10.dp)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.weight(1f)) {
                            MemberList(viewModel = ulVm, addGroupScreenViewModel = agVm, context = context)
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun MemberList(viewModel: UserListViewModel, addGroupScreenViewModel: AddGroupScreenViewModel, context: Context) {

    val contacts = viewModel.contacts.collectAsState()
    val currentUser = viewModel.currentUser.collectAsState()
    val participants = addGroupScreenViewModel.participants

    CustomMemberListView(addGroupScreenViewModel, context)

    if(contacts.value.isNotEmpty()) {
        Text(
            text = "CONTACT LIST",
            modifier = Modifier
                .padding(top = 125.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(top = 145.dp),
                //.padding(start = 4.dp, end = 4.dp),
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(contacts.value) { position, contact ->
                    val isSelected = participants.contains(contact)
                    UserCard(
                        position = position,
                        otherUser = contact,
                        currentUser = currentUser.value,
                        isSelected = isSelected,
                        onChatClicked = {
                            addGroupScreenViewModel.onChatClicked(contact)
                        }
                    )
                    Divider()
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomMemberListView(agVm: AddGroupScreenViewModel, context: Context) {
    LazyRow {
        itemsIndexed(agVm.participants) { index, item ->
            Card(
                onClick = {
                    Toast.makeText(
                        context,
                        agVm.participants[index].email + " selected..",
                        Toast.LENGTH_SHORT
                    ).show()
                },

                modifier = Modifier
                    .padding(8.dp)
                    .width(120.dp),
                elevation = 6.dp
            )
            {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // in the below line, inside row we are adding spacer
                    Spacer(modifier = Modifier.height(5.dp))

                    Image(
                        modifier = Modifier
                            .size(PROFILE_PICTURE_SIZE.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(MR.images.stock2.drawableResId),
                        contentDescription = "User picture"
                    )

                    Text(
                        text = item.email!!,
                        modifier = Modifier.padding(4.dp),
                        color = Color.Black, textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun AddMemberToGroupButtonCompose(addGroupScreenViewModel: AddGroupScreenViewModel, groupId: String){

    FloatingActionButton(
        shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 40)),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        onClick = { addGroupScreenViewModel.addMemberToGroup(groupId) }
    ) {
        Icon(Icons.Default.ArrowForward, contentDescription = null)
    }
}