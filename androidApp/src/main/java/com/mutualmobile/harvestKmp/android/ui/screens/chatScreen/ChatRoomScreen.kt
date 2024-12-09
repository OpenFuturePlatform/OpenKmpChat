package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultGroupPicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.LoadingIndicator
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.theme.Typography
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.ChatRoomScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import org.koin.androidx.compose.get
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatRoomScreen(
    navController: NavHostController,
    crVm: ChatRoomScreenViewModel = get(),
    userState: OpenDataModel.DataState
) {

    val scaffoldState = rememberScaffoldState()

    val mContext = LocalContext.current

    LaunchedEffect(crVm.currentNavigationCommand) {
        when (crVm.currentNavigationCommand) {
            is NavigationOpenCommand -> {
                val destination = (crVm.currentNavigationCommand as NavigationOpenCommand).screen
                crVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is OpenDataModel.SuccessState<*> -> {
                crVm.getUserGroupChats(userState = userState)
            }

            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OpenAiX Team") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        },
        floatingActionButton = { FloatingActionButtonCompose(mContext, navController) },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true,
        scaffoldState = scaffoldState
    ) {
        OpenChatTheme {
            Surface {
                Box(modifier = Modifier.fillMaxSize().then(Modifier.padding(it))) {
                    Image(
                        painterResource(MR.images.background.drawableResId),
                        null,
                        contentScale = ContentScale.FillHeight
                    )
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatsBody(viewModel: ChatRoomScreenViewModel) {
    val chats = viewModel.chats.collectAsState()
    if (chats.value.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(chats.value) { position, chat ->
                if (chat.group) {
                    GroupChatCard(
                        position = position,
                        groupName = chat.chatRoomName,
                        lastMessageText = chat.lastMessageText!!,
                        onChatClicked = viewModel::onChatClicked
                    )
                } else {
                    OneToOneChatCard(
                        position = position,
                        otherUserName = chat.chatRoomName,
                        lastMessageText = chat.lastMessageText!!,
                        lastMessageAt = chat.lastMessageTime,
                        otherUserPicture = chat.chatRoomPicture,
                        onChatClicked = viewModel::onChatClicked
                    )
                }
                Divider()
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OneToOneChatCard(
    position: Int,
    otherUserName: String,
    lastMessageText: String,
    lastMessageAt: LocalDateTime?,
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
//                if(otherUserPicture != null) {
//                    ProfilePicture(pictureString = otherUserPicture, displayName = otherUserName)
//                } else {
//                    DefaultProfilePicture(displayName = otherUserName)
//                }
                DefaultProfilePicture(displayName = otherUserName)

                Column(modifier = Modifier.padding(start = 15.dp)) {
                    Text(
                        text = otherUserName,
                        style = Typography.subtitle2,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = lastMessageText,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = convertDateTimeToString(lastMessageAt!!),
                    style = MaterialTheme.typography.overline,
                    modifier = Modifier
                        .padding(top = 35.dp)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                //displays last message
//                Text(
//                    text = lastMessageText,
//                    style = MaterialTheme.typography.body2,
//                    maxLines = 1,
//                    textAlign = TextAlign.Right,
//                    overflow = TextOverflow.Ellipsis,
//                    modifier = Modifier.padding(start = 36.dp)
//                )
//                //message send time
//                Text(
//                    text =  lastMessageAt.toString(),
//                    style = MaterialTheme.typography.overline,
//                    modifier = Modifier
//                        .padding(top = 2.dp)
//                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertDateTimeToString(date: LocalDateTime): String {
    val CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return date.toJavaLocalDateTime().format(CUSTOM_FORMATTER)
}

@Composable
fun FloatingActionButtonCompose(context: Context, navController: NavHostController) {

    FloatingActionButton(
        shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 40)),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        onClick = { navController.navigate(HarvestRoutes.Screen.ADD_ACTION) }
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}

