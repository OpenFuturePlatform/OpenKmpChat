package com.mutualmobile.harvestKmp.android.ui.screens.common

import android.R
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.DatePicker
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.serverDateFormatter
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.TaskScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.data.network.PROFILE_PICTURE_SIZE
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.domain.model.response.AssistantNotesResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantReminderResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantTodosResponse
import com.mutualmobile.harvestKmp.utils.now
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.get
import wallet.core.jni.Blockchain
import wallet.core.jni.CoinType
import java.util.*

@Composable
fun CommonAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleProvider: @Composable () -> String,
    bodyTextProvider: @Composable () -> String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        text = {
            Text(text = bodyTextProvider(), style = MaterialTheme.typography.body1)
        },
        title = {
            Text(text = titleProvider(), style = MaterialTheme.typography.h6)
        }
    )
}

@Composable
fun AssistantNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleProvider: @Composable () -> String,
    currentAssistant: String,
    assistantNotes: List<AssistantNotesResponse>,
    assistantReminders: List<AssistantReminderResponse>,
    assistantTodos: List<AssistantTodosResponse>,
    tsVm: TaskScreenViewModel = get(),
) {

    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                modifier = Modifier
                    .defaultMinSize(minHeight = 72.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                item { Spacer(Modifier.size(20.dp)) }

                when (currentAssistant) {
                    "NOTES" -> items(assistantNotes) {
                        AssistantNote(it)
                    }

                    "REMINDERS" -> items(assistantReminders) {
                        AssistantReminder(it)
                    }

                    else -> items(assistantTodos) {
                        AssistantToDo(tsVm, it)
                    }
                }

                item {
                    Box(Modifier.height(70.dp))
                }
            }

            Row(
                modifier = Modifier
                    //.fillMaxWidth()
                    .background(color = Color.White),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Ok",
                    )
                }
            }
        }
    }
}

@Composable
fun AssistantNote(assistantNotesResponse: AssistantNotesResponse) {

    Column {
        Text(
            text = assistantNotesResponse.startTime.date.toString() + " - " + assistantNotesResponse.endTime.date.toString(),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.size(24.dp))
        assistantNotesResponse.notes.forEach { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }

}

@Composable
fun AssistantReminder(assistantReminderResponse: AssistantReminderResponse) {

    Column {
        Text(
            text = assistantReminderResponse.startTime.date.toString() + " - " + assistantReminderResponse.endTime.date.toString(),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.size(24.dp))
        assistantReminderResponse.reminders.forEach { message ->
            if (message.remindAt != null) {
                Text(
                    text = message.remindAt.toString(),
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onPrimary
                )
            }
            Text(
                text = message.description!!,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }

}

@Composable
fun AssistantToDo(
    tsVm: TaskScreenViewModel,
    assistantTodosResponse: AssistantTodosResponse
) {

    Column {
        Text(
            text = assistantTodosResponse.startTime.date.toString() + " - " + assistantTodosResponse.endTime.date.toString(),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.size(24.dp))
        assistantTodosResponse.todos.forEach { message ->
            Column {
                Text(
                    text = message.executor!!,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.onPrimary
                )
                Text(
                    text = message.context!!,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSecondary
                )
                Text(
                    text = message.description!!,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSecondary
                )
            }
            Column {
                TextButton(modifier = Modifier
                    .background(
                        color = Color.White
                    ),
                    onClick = {
                        tsVm.saveUserTasks(
                            taskRequest = TaskRequest(
                                assignor = null,
                                assignee = message.executor!!,
                                taskTitle = message.context!!,
                                taskDescription = message.description!!,
                                taskDate = LocalDate.now()
                            )
                        )
                    }
                ) {
                    Text(
                        text = "Assign",
                    )
                }
            }

        }
    }

}

@Composable
fun AssistantDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleProvider: @Composable () -> String,
    startDateProvider: (Date) -> Unit,
    endDateProvider: (Date) -> Unit
) {

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    var startDatePickerButton by remember {
        mutableStateOf(false)
    }
    var endDatePickerButton by remember {
        mutableStateOf(false)
    }
    if (startDatePickerButton) {
        DatePicker(onDateSelected = {
            println("Selected Start Date : $it")
            startDate = serverDateFormatter.format(it)
            startDateProvider(it)
        }, onDismissRequest = { startDatePickerButton = false })
    }
    if (endDatePickerButton) {
        DatePicker(onDateSelected = {
            println("Selected End Date : $it")
            endDate = serverDateFormatter.format(it)
            endDateProvider(it)
        }, onDismissRequest = { endDatePickerButton = false })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        text = {

            Row(

            ) {
                Button(onClick = {
                    startDatePickerButton = true
                }, modifier = Modifier.padding(1.dp)) {
                    if (startDate == "")
                        Text(text = "Start Date")
                    else
                        Text(text = startDate)
                }
                Button(onClick = {
                    endDatePickerButton = true
                }, modifier = Modifier.padding(1.dp)) {
                    if (endDate == "")
                        Text(text = "End Date")
                    else
                        Text(text = endDate)
                }
            }

        },
        title = {
            Text(text = titleProvider(), style = MaterialTheme.typography.h6)
        }
    )
}

@Preview
@Composable
fun CommonAlertDialogPreview() {
    OpenChatTheme {
        CommonAlertDialog(
            onDismiss = {},
            onConfirm = {},
            titleProvider = { MR.strings.delete_work_dialog_title.get() },
            bodyTextProvider = { MR.strings.delete_work_dialog_bodyText.get() }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateWalletDialog(
    networks: List<String>,
    selectedNetworks: List<String>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    wsVm: WalletScreenViewModel = get(),
) {

    var expanded by remember { mutableStateOf(false) }

    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf("") }
    val selectedNetworks = remember { mutableStateListOf(*selectedNetworks.toTypedArray()) }

    Dialog(onDismissRequest = { onDismiss() }, properties = DialogProperties()) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Generate wallet",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "",
                            tint = colorResource(R.color.darker_gray),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { wsVm.isWalletGenerateDialogVisible = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    ExposedDropdownMenuBox(
                        modifier = Modifier.fillMaxWidth()
                            .border(
                                BorderStroke(
                                    width = 2.dp,
                                    color = colorResource(id = if (txtFieldError.value.isEmpty()) R.color.holo_green_light else R.color.holo_red_dark)
                                )
                            ),
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = if (selectedNetworks.size == 0) "Choose Network" else selectedNetworks.joinToString(", "),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            networks.forEach { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = selectedNetworks.contains(item),
                                        onCheckedChange = {
                                            if (it) selectedNetworks.add(item) else selectedNetworks.remove(item)
                                        }
                                    )
                                    Text(text = item)
                                }
                            }
                        }

                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(
                                    width = 2.dp,
                                    color = colorResource(id = if (txtFieldError.value.isEmpty()) R.color.holo_green_light else R.color.holo_red_dark)
                                )
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text(text = "Enter password") },
                        value = txtField.value,
                        onValueChange = {
                            txtField.value = it.take(10)
                        })

                    Spacer(modifier = Modifier.height(20.dp))

                    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                        Button(
                            onClick = {
                                if (txtField.value.isEmpty()) {
                                    txtFieldError.value = "Password can not be empty"
                                    return@Button
                                }
                                wsVm.password = txtField.value
                                wsVm.isWalletGenerateDialogVisible = false
                                wsVm.blockchainNetworks = selectedNetworks.map { BlockchainType.valueOf(it.toString()) }
                                // confirm
                                onConfirm()
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(text = "Generate")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MultiSelectDialog(
    items: List<String>,
    selectedItems: List<String>,
    onDismissRequest: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val selected = remember { mutableStateListOf(*selectedItems.toTypedArray()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Select Items") },
        text = {
            Column {
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selected.contains(item),
                            onCheckedChange = {
                                if (it) selected.add(item) else selected.remove(item)
                            }
                        )
                        Text(text = item)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selected) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun WalletDetailDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSign: () -> Unit,
    titleProvider: @Composable () -> String,
    wsVm: WalletScreenViewModel
) {
    val clipboardManager = LocalClipboardManager.current
    val txtFieldError = remember { mutableStateOf("") }
    val txtField = remember { mutableStateOf("") }

    Dialog(onDismissRequest = {
        onDismiss()
    }, properties = DialogProperties()) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wallet Detail",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "",
                            tint = colorResource(R.color.darker_gray),
                            modifier = Modifier
                                .width(30.dp)
                                .height(30.dp)
                                .clickable { onDismiss() }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = wsVm.currentWalletAddress,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    Button(onClick = {
                        clipboardManager.setText(AnnotatedString((wsVm.currentWalletAddress)))
                    }) {
                        Text("Copy Address")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (wsVm.currentWalletDecryptedPrivateKey != "") {
//                        Text(
//                            text = wsVm.currentWalletSeeedPhrases,
//                            fontSize = 18.sp,
//                            color = Color.Black,
//                            modifier = Modifier.padding(all = 8.dp)
//                        )
                        Text(
                            text = wsVm.currentWalletDecryptedPrivateKey,
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(all = 8.dp)
                        )
                        Button(onClick = {
                            onSign()
                        }) {
                            Text(text = "SEND")
                        }
                        Button(onClick = {
                            clipboardManager.setText(AnnotatedString((wsVm.currentWalletDecryptedPrivateKey)))
                        }) {
                            Text("Copy Private Key")
                        }

                    }
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(
                                    width = 2.dp,
                                    color = colorResource(id = if (txtFieldError.value.isEmpty()) R.color.holo_green_light else R.color.holo_red_dark)
                                )
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text(text = "Enter password to decrypt") },
                        value = txtField.value,
                        onValueChange = {
                            txtField.value = it.take(10)
                        })

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            onDismiss()
                        }) {
                            Text(text = "CANCEL")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(onClick = {
                            if (txtField.value.isEmpty()) {
                                txtFieldError.value = "Password can not be empty"
                                return@Button
                            }
                            wsVm.password = txtField.value
                            onConfirm()
                        }) {
                            Text(text = "DECRYPT")
                        }


                    }

//                    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
//                        Button(
//                            onClick = {
//                                if (txtField.value.isEmpty()) {
//                                    txtFieldError.value = "Password can not be empty"
//                                    return@Button
//                                }
//                                wsVm.password = txtField.value
//
//                                onConfirm()
//                            },
//                            shape = RoundedCornerShape(50.dp),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(50.dp)
//                        ) {
//                            Text(text = "Done")
//                        }
//                    }
                }
            }
        }
    }
}

//Layout
@Composable
fun CustomDialogUI(modifier: Modifier = Modifier, openDialogCustom: MutableState<Boolean>) {
    Card(
        //shape = MaterialTheme.shapes.medium,
        shape = RoundedCornerShape(10.dp),
        // modifier = modifier.size(280.dp, 240.dp)
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
        elevation = 8.dp
    ) {
        Column(
            modifier
                .background(Color.White)
        ) {


            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Get Updates",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h4,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Allow Permission to send you notifications when new art styles added.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 10.dp, start = 25.dp, end = 25.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h5
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(Color.Cyan),
                horizontalArrangement = Arrangement.SpaceAround
            ) {

                TextButton(onClick = {
                    openDialogCustom.value = false
                }) {

                    Text(
                        "Not Now",
                        fontWeight = FontWeight.Bold,
                        color = Color.Cyan,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(onClick = {
                    openDialogCustom.value = false
                }) {
                    Text(
                        "Allow",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}