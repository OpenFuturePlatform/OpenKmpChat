package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.GroupAdd
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Message
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.LoadingIndicator
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.theme.Typography
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.UserListViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.request.User
import org.koin.androidx.compose.get


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    ulVm: UserListViewModel = get(),
    userState: OpenDataModel.DataState
) {

    println("ADD SCREEN")

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
            is OpenDataModel.SuccessState<*> -> { ulVm.getUserContacts(userState) }
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
                title = { Text("New Message") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        }
    ) {
        OpenChatTheme {
            Surface {
                Box(modifier = Modifier.fillMaxSize()
                    .then(Modifier.padding(it))
                ) {
                    Image(painterResource(
                        MR.images.background.drawableResId),
                        null,
                        contentScale = ContentScale.FillHeight
                    )

                    LoadingIndicator(loadingFlow = ulVm.loading)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            //.padding(all = 4.dp)
                            .clickable { navController.navigate(HarvestRoutes.Screen.ADD_GROUP) }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center
                        ) {

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DefaultLogoPicture()
                                Text(
                                    text = "Create New Group",
                                    style = Typography.subtitle2,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }

                    //Divider(thickness = 10.dp)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.weight(1f)) {
                            AddsBody(viewModel = ulVm)
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun AddsBody(viewModel: UserListViewModel) {

    val contacts = viewModel.contacts.collectAsState()
    val currentUser = viewModel.currentUser.collectAsState()


    if(contacts.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(top = 45.dp),
                //.padding(start = 4.dp, end = 4.dp),
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(contacts.value) { position, contact ->

                    UserCard(
                        position = position,
                        otherUser = contact,
                        currentUser = currentUser.value,
                        isSelected = false,
                        onChatClicked = viewModel::onChatClicked
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



@Composable
fun UserCard(
    position: Int,
    otherUser: User,
    currentUser: User,
    isSelected: Boolean,
    onChatClicked: (Int) -> Unit
) {
    val isYourself = otherUser.email.equals(currentUser.email)

    if (!isYourself) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                //.padding(all = 5.dp)
                .clickable { onChatClicked.invoke(position) }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp)
                ) {

                    DefaultProfilePicture(displayName = otherUser.email!!)

                    if (isSelected) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.background(MaterialTheme.colors.primary))
                    }
                    Text(
                        text = otherUser.email!!,
                        style = Typography.subtitle2,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                }
            }
        }
    }
}

@Composable
fun DefaultLogoPicture() {
    Icon(
        imageVector = Icons.Rounded.GroupAdd,
        contentDescription = null,
        modifier = Modifier
           .size(PROFILE_PICTURE_SIZE.dp)
           .clip(CircleShape)
    )
}