package com.mutualmobile.harvestKmp.android.ui.screens.userScreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.request.User
import org.koin.androidx.compose.get


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserListScreen(
    navController: NavHostController,
    ulVm: UserListViewModel = get(),
    userState: OpenDataModel.DataState
) {

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
                title = { Text("User List") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        }
    ) {
        OpenChatTheme {
            Surface {
                Box(modifier = Modifier.fillMaxSize().then(Modifier.padding(it))) {
                    Image(painterResource(
                        MR.images.background.drawableResId),
                        null,
                        contentScale = ContentScale.FillHeight)

                    LoadingIndicator(loadingFlow = ulVm.loading)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.weight(1f)) {
                            ContactsBody(viewModel = ulVm)
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun ContactsBody(viewModel: UserListViewModel) {
    val contacts = viewModel.contacts.collectAsState()
    val currentUser = viewModel.currentUser.collectAsState()
    if(contacts.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(start = 4.dp, end = 4.dp),
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(contacts.value) { position, contact ->

                    ContactCard(
                        position = position,
                        otherUser = contact,
                        currentUser = currentUser.value,
                        onChatClicked = viewModel::onChatClicked
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



@Composable
fun ContactCard(
    position: Int,
    otherUser: User,
    currentUser: User,
    onChatClicked: (Int) -> Unit
) {
    val isYourself = otherUser.email.equals(currentUser.email)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp)
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
                Text(
                    text = otherUser.email!!,
                    style = Typography.subtitle2,
                    modifier = Modifier.padding(start = 16.dp)
                )

            }
            if (isYourself){
                Text(
                    text = "Message to yourself",
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

        }
    }
}