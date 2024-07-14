package com.mutualmobile.harvestKmp.android.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.DatePicker
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.serverDateFormatter
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.get
import com.mutualmobile.harvestKmp.android.viewmodels.TaskScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.WalletScreenViewModel
import com.mutualmobile.harvestKmp.domain.model.request.TaskRequest
import com.mutualmobile.harvestKmp.domain.model.response.AssistantNotesResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantReminderResponse
import com.mutualmobile.harvestKmp.domain.model.response.AssistantTodosResponse
import com.mutualmobile.harvestKmp.utils.now
import kotlinx.datetime.LocalDate
import org.koin.androidx.compose.get
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
                        tsVm.saveUserTasks(taskRequest = TaskRequest(
                            assignor = null,
                            assignee = message.executor!!,
                            taskTitle =  message.context!!,
                            taskDescription = message.description!!,
                            taskDate = LocalDate.now()
                        ))
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

@Composable
fun GenerateWalletDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleProvider: @Composable () -> String,
    currentUser: String,
    tsVm: WalletScreenViewModel = get(),
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