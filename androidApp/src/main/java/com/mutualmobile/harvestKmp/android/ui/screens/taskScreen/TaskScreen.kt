package com.mutualmobile.harvestKmp.android.ui.screens.taskScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.common.HarvestDialog
import com.mutualmobile.harvestKmp.android.ui.screens.taskScreen.components.TaskListItem
import com.mutualmobile.harvestKmp.android.ui.screens.taskScreen.components.TaskSearchView
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.NewEntryScreenViewModel
import com.mutualmobile.harvestKmp.android.viewmodels.TaskScreenViewModel
import com.mutualmobile.harvestKmp.datamodel.HarvestRoutes
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel.*
import org.koin.androidx.compose.get


@Composable
fun TaskScreen(
    navController: NavHostController,
    nesVm: NewEntryScreenViewModel = get(),
    userState: DataState,
    psVm: TaskScreenViewModel = get(),
) {
    LaunchedEffect(psVm.taskScreenNavigationCommands) {
        when (psVm.taskScreenNavigationCommands) {
            is NavigationPraxisCommand -> {
                if ((psVm.taskScreenNavigationCommands as NavigationPraxisCommand).screen.isBlank()) {
                    navController clearBackStackAndNavigateTo HarvestRoutes.Screen.FIND_WORKSPACE
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is SuccessState<*> -> { psVm.getUserTasks(userState = userState) }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = MR.strings.choose_task.resourceId),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    TaskSearchView(psVm.textState) { updatedState ->
                        psVm.textState = updatedState
                    }
                },
                contentPadding = WindowInsets.statusBars.asPaddingValues(),
            )
        },

        ) { bodyPadding ->

        Column(modifier = Modifier.padding(bodyPadding)) {
            AnimatedVisibility(visible = psVm.currentTaskScreenState is LoadingState) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                val searchedText = psVm.textState.text
                psVm.filteredTaskListMap = if (searchedText.isEmpty()) {
                    psVm.taskListMap
                } else {
                    psVm.taskListMap.filter { it.taskTitle?.contains(searchedText, true) == true }
                }
                items(psVm.filteredTaskListMap) { task ->
                    TaskListItem(
                        label = task.taskTitle ?: "",
                        onItemClick = { selectedTask ->
                            nesVm.updateCurrentProjectName(selectedTask)
                        })
                }

            }
        }
        HarvestDialog(praxisCommand = psVm.taskScreenNavigationCommands, onConfirm = {
            psVm.taskScreenNavigationCommands = null
        })
    }
}