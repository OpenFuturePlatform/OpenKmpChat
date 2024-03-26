package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultGroupPicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.LoadingIndicator
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.ProfilePicture
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.theme.Typography
import com.mutualmobile.harvestKmp.android.ui.theme.spacing
import com.mutualmobile.harvestKmp.android.viewmodels.ChatRoomViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.UserHomeViewModel
import com.mutualmobile.harvestKmp.domain.model.DisplayChatRoom
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import org.koin.androidx.compose.get



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatRoomScreen(
    navController: NavHostController,
    crVm: ChatRoomViewModel = get(),
    user: GetUserResponse?,
    modifier: Modifier
) {

    val scaffoldState = rememberScaffoldState()
    val lazyColumnState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val loading = crVm.loading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open Chat") },
                backgroundColor = MaterialTheme.colors.primary,
                //modifier = modifier.navigationBarsPadding()
            )
        }
    ) {
        OpenChatTheme {
            Surface {
                Box(modifier = Modifier.fillMaxSize().then(Modifier.padding(it))) {
                    Image(painterResource(MR.images.background.drawableResId), null, contentScale = ContentScale.Crop)
                    LoadingIndicator(loadingFlow = crVm.loading)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.weight(1f)) {
                            ChatsBody(viewModel = crVm)
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun ChatsBody(viewModel: ChatRoomViewModel) {
    val chats = viewModel.chats.collectAsState()
    if(chats.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
                //.padding(start = 4.dp, end = 4.dp),
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(chats.value) { position, chat ->
                if(chat.group) {
                    GroupChatCard(
                        position = position,
                        groupName = chat.chatRoomName,
                        lastMessageText = chat.lastMessageText!!,
                        onChatClicked = viewModel::onChatClicked
                    )
                } else {
                    OneToOneChatCard(
                        position = position,
                        otherUserName = chat.displayUserName,
                        lastMessageText = chat.lastMessageText!!,
                        otherUserPicture = chat.chatRoomPicture,
                        onChatClicked = viewModel::onChatClicked
                    )
                }
            }
            item {
                Box(Modifier.height(70.dp))
            }
        }
    } else {
        Text(
            text = stringResource(id = MR.strings.home_no_chats.resourceId),
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GroupChatCard(
    position: Int,
    groupName: String,
    lastMessageText: String,
    onChatClicked: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            //.padding(horizontal = 16.dp, vertical = 8.dp)
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
                //TODO: when implemented, this can be replaced with other user picture
//                Icon(
//                    painter = painterResource(id = MR.images.stock2.drawableResId),
//                    contentDescription = groupName
//                )
                DefaultGroupPicture(displayName = groupName)
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
//                Text(
//                    text = stringResource(id = MR.strings.home_last_message.resourceId),
//                    style = MaterialTheme.typography.body2,
//                    maxLines = 1,
//                    modifier = Modifier.padding(start = 16.dp)
//                )
                //displays last message
                Text(
                    text = lastMessageText,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OneToOneChatCard(
    position: Int,
    otherUserName: String,
    lastMessageText: String,
    otherUserPicture: String?,
    onChatClicked: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                if(otherUserPicture != null) {
                    ProfilePicture(pictureString = otherUserPicture, displayName = otherUserName)
                } else {
                    DefaultProfilePicture(displayName = otherUserName)
                }
                Text(
                    text = otherUserName,
                    style = Typography.subtitle2,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(top = 8.dp, bottom = 8.dp)
            ) {
                //displays last message
                Text(
                    text = lastMessageText,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    //overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

        }
    }
}
