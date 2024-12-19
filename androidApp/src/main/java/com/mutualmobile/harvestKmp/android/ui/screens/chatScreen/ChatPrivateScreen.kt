package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import CustomLinearProgressBar
import SendMessage
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Queue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.ChatInput
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.Messages
import com.mutualmobile.harvestKmp.android.ui.screens.common.AssistantDatePickerDialog
import com.mutualmobile.harvestKmp.android.ui.screens.common.AssistantNoteDialog
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultGroupPicture
import com.mutualmobile.harvestKmp.android.ui.screens.loginScreen.DefaultProfilePicture
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.serverDateFormatter
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.ChatPrivateScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.MainActivityViewModel
import com.mutualmobile.harvestKmp.datamodel.NavigationOpenCommand
import com.mutualmobile.harvestKmp.datamodel.OpenDataModel
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.TextType
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.androidx.compose.get

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun ChatPrivateScreen(
    navController: NavHostController,
    crVm: ChatPrivateScreenViewModel = get(),
    user: GetUserResponse?,
    userState: OpenDataModel.DataState,
    recipient: String?,
    sender: String?,
    isGroup: String?,
    chatUid: String?
) {
    val context = LocalContext.current
    println("ChatPrivateScreen: recipient: $recipient, sender: $sender, isGroup: $isGroup, chatUid: $chatUid")

    val scaffoldState = rememberScaffoldState()
    val canSendMessage = crVm.canSendMessage

    var notesMenuExpanded by remember {
        mutableStateOf(false)
    }
    var remindersMenuExpanded by remember {
        mutableStateOf(false)
    }
    var todosMenuExpanded by remember {
        mutableStateOf(false)
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
                    ContactProfileNavigation(
                        isGroup = isGroup == "true",
                        recipient = recipient!!,
                        chatUid = chatUid,
                        onChatClicked = crVm::onContactProfileClicked
                    )
                },
                actions = {

                    IconButton(onClick = { remindersMenuExpanded = !remindersMenuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.NotificationImportant,
                            contentDescription = "Reminders"
                        )
                    }
                    IconButton(onClick = { notesMenuExpanded = !notesMenuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Note,
                            contentDescription = "Notes"
                        )
                    }
                    IconButton(onClick = { todosMenuExpanded = !todosMenuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.Queue,
                            contentDescription = "ToDos",
                        )
                    }
                    DropdownMenu(
                        expanded = notesMenuExpanded,
                        onDismissRequest = { notesMenuExpanded = false },
                    ) {
                        DropdownMenuItem(

                            content = {
                                Text("Get Notes")
                            },
                            onClick = {
                                crVm.getAssistantNotes(chatUid!!, isGroup == "true")
                            },
                        )
                        DropdownMenuItem(

                            content = {
                                Text("Take Notes")
                            },
                            onClick = {
                                crVm.isAssistantNotesDialogVisible = true
                                crVm.assistantConfirmChatId = chatUid!!
                                crVm.assistantConfirmIsGroup = isGroup!!
                            },
                        )

                    }
                    DropdownMenu(
                        expanded = remindersMenuExpanded,
                        onDismissRequest = { remindersMenuExpanded = false },
                    ) {
                        DropdownMenuItem(

                            content = {
                                Text("Get Reminders")
                            },
                            onClick = {
                                crVm.getAssistantReminders(chatUid!!, isGroup == "true")
                            },
                        )
                        DropdownMenuItem(

                            content = {
                                Text("Take Reminders")
                            },
                            onClick = {
                                crVm.isAssistantRemindersDialogVisible = true
                                crVm.assistantConfirmChatId = chatUid!!
                                crVm.assistantConfirmIsGroup = isGroup!!
                            },
                        )
                    }
                    DropdownMenu(
                        expanded = todosMenuExpanded,
                        onDismissRequest = { todosMenuExpanded = false },
                    ) {
                        DropdownMenuItem(

                            content = {
                                Text("Get ToDos")
                            },
                            onClick = {
                                crVm.getAssistantToDos(chatUid!!, isGroup == "true")
                            },
                        )
                        DropdownMenuItem(

                            content = {
                                Text("Take ToDos")
                            },
                            onClick = {
                                crVm.isAssistantToDosDialogVisible = true
                                crVm.assistantConfirmChatId = chatUid!!
                                crVm.assistantConfirmIsGroup = isGroup!!
                            },
                        )
                    }
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },
        scaffoldState = scaffoldState
    ) { padding ->
        ChatPrivateApp(
            viewModel = crVm,
            canSendMessage = canSendMessage,
            modifier = Modifier.padding(0.dp),
            user = user,
            recipient = recipient,
            isGroup = isGroup == "true",
            groupChatId = chatUid!!
        )
    }

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

    LaunchedEffect(Unit) {

        when (userState) {
            is OpenDataModel.SuccessState<*> -> {
                if (chatUid!! == recipient!!) {
                    crVm.getPrivateChats(recipient, sender!!)
                } else {
                    crVm.getPrivateChats(chatUid, isGroup == "true", recipient, sender!!)
                }
            }

            else -> Unit
        }
    }

    if (crVm.isAssistantNotesDialogVisible) {
        AssistantDatePickerDialog(
            onDismiss = {
                crVm.isAssistantNotesDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
            },
            onConfirm = {
                crVm.addAssistantNotes()
                crVm.isAssistantNotesDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
                Toast.makeText(
                    context,
                    "STARTED GETTING NOTES",
                    Toast.LENGTH_SHORT
                ).show()
            },
            titleProvider = { MR.strings.assistant_datepicker_title.get() },
            startDateProvider = {
                crVm.assistantStartDate = serverDateFormatter.format(it)
            },
            endDateProvider = {
                crVm.assistantEndDate = serverDateFormatter.format(it)
            }

        )
    }
    if (crVm.isAssistantRemindersDialogVisible) {
        AssistantDatePickerDialog(
            onDismiss = {
                crVm.isAssistantRemindersDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
            },
            onConfirm = {
                crVm.addAssistantReminders()
                crVm.isAssistantRemindersDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
                Toast.makeText(
                    context,
                    "STARTED GETTING REMINDERS",
                    Toast.LENGTH_SHORT
                ).show()
            },
            titleProvider = { MR.strings.assistant_datepicker_title.get() },
            startDateProvider = {
                crVm.assistantStartDate = serverDateFormatter.format(it)
            },
            endDateProvider = {
                crVm.assistantEndDate = serverDateFormatter.format(it)
            }

        )
    }
    if (crVm.isAssistantToDosDialogVisible) {
        AssistantDatePickerDialog(
            onDismiss = {
                crVm.isAssistantToDosDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
            },
            onConfirm = {
                crVm.addAssistantToDos()
                crVm.isAssistantToDosDialogVisible = false
                crVm.assistantConfirmChatId = ""
                crVm.assistantConfirmIsGroup = ""
                Toast.makeText(
                    context,
                    "STARTED GETTING TODOS",
                    Toast.LENGTH_SHORT
                ).show()
            },
            titleProvider = { MR.strings.assistant_datepicker_title.get() },
            startDateProvider = {
                crVm.assistantStartDate = serverDateFormatter.format(it)
            },
            endDateProvider = {
                crVm.assistantEndDate = serverDateFormatter.format(it)
            }

        )
    }
    if (crVm.assistantNotesReady) {

        AssistantNoteDialog(
            onDismiss = {
                crVm.assistantNotesReady = false
                crVm.assistantNotes = emptyList()
                crVm.assistantTodos = emptyList()
                crVm.assistantReminders = emptyList()
                crVm.currentAssistantType = ""
            },
            onConfirm = {
                crVm.assistantNotesReady = false
                crVm.assistantNotes = emptyList()
                crVm.assistantTodos = emptyList()
                crVm.assistantReminders = emptyList()
                crVm.currentAssistantType = ""
            },
            titleProvider = { MR.strings.assistant_notes.get() },
            currentAssistant = crVm.currentAssistantType,
            assistantNotes = crVm.assistantNotes,
            assistantReminders = crVm.assistantReminders,
            assistantTodos = crVm.assistantTodos
        )

    }
}

@Composable
fun ContactProfileNavigation(
    isGroup: Boolean,
    recipient: String?,
    chatUid: String?,
    onChatClicked: (String, Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize().padding(5.dp)
        .clickable {
            if (isGroup)
                onChatClicked.invoke(chatUid!!, true)
            else
                onChatClicked.invoke(recipient!!, false)
        }
    ) {
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ChatPrivateApp(
    displayTextField: Boolean = true,
    viewModel: ChatPrivateScreenViewModel = get(),
    canSendMessage: Boolean,
    modifier: Modifier,
    user: GetUserResponse?,
    isGroup: Boolean,
    groupChatId: String,
    recipient: String?
) {

    val mainActivityViewModel: MainActivityViewModel = get()
    mainActivityViewModel.user
    val myUser = ChatUser(user?.id!!, user.firstName!!, email = user.email!!, picture = null)
    val messages = viewModel.chats

    OpenChatTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(MR.images.background.drawableResId),
                    null,
                    contentScale = ContentScale.FillHeight
                )
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(Modifier.weight(1f)) {
                        Messages(messages, myUser)
                    }
                    if (!canSendMessage) {
                        CustomLinearProgressBar()
                    }
                    if (displayTextField) {
                        ChatInput(
                            onMessageChange = { text, type, imageByteArray, imageCheckSum ->
                                val receiver = if (isGroup) groupChatId else recipient!!
                                if (type == TextType.ATTACHMENT) {
                                    viewModel.uploadAttachment(
                                        imageByteArray!!,
                                        imageCheckSum!!,
                                        Clock.System.now().epochSeconds.toString(),
                                        Message(myUser, recipient = receiver, text, emptyList(), type),
                                        isGroup
                                    )
                                } else if (isGroup) {
                                    viewModel.saveGroupChat(
                                        Message(
                                            myUser,
                                            recipient = groupChatId,
                                            text,
                                            emptyList(),
                                            TextType.TEXT
                                        )
                                    )
                                } else {
                                    viewModel.savePrivateChat(
                                        Message(
                                            myUser,
                                            recipient = recipient!!,
                                            text,
                                            emptyList(),
                                            TextType.TEXT
                                        )
                                    )
                                }

                            }
                        )


//                        SendMessage { text, type, imageByteArray, imageCheckSum ->
//                            val receiver = if (isGroup) groupChatId else recipient!!
//                            if (type == TextType.ATTACHMENT) {
//                                viewModel.uploadAttachment(
//                                    imageByteArray!!,
//                                    imageCheckSum!!,
//                                    Clock.System.now().epochSeconds.toString(),
//                                    Message(myUser, recipient = receiver, text, emptyList(), type),
//                                    isGroup
//                                )
//                            } else if (isGroup) {
//                                viewModel.saveGroupChat(
//                                    Message(
//                                        myUser,
//                                        recipient = groupChatId,
//                                        text,
//                                        emptyList(),
//                                        type
//                                    )
//                                )
//                            } else {
//                                viewModel.savePrivateChat(
//                                    Message(
//                                        myUser,
//                                        recipient = recipient!!,
//                                        text,
//                                        emptyList(),
//                                        type
//                                    )
//                                )
//                            }
//                        }
                    }
                }
            }
        }
    }
}