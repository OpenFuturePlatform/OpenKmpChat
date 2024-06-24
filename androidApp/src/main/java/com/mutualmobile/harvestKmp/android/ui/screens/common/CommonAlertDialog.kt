package com.mutualmobile.harvestKmp.android.ui.screens.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.DatePicker
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.formatter
import com.mutualmobile.harvestKmp.android.ui.screens.newEntryScreen.components.serverDateFormatter
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.get
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
fun AssistantDatePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    titleProvider: @Composable () -> String,
    startDateProvider: (Date) -> Unit,
    endDateProvider:  (Date) -> Unit
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