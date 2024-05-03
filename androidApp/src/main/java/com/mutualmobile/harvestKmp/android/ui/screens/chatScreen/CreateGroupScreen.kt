package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ui.TopAppBar
import com.mutualmobile.harvestKmp.MR
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.GreateGroupTextField
import com.mutualmobile.harvestKmp.android.ui.theme.OpenChatTheme
import com.mutualmobile.harvestKmp.android.ui.utils.clearBackStackAndNavigateTo
import com.mutualmobile.harvestKmp.android.viewmodels.AddGroupViewModel
import com.mutualmobile.harvestKmp.datamodel.NavigationPraxisCommand
import com.mutualmobile.harvestKmp.datamodel.PraxisDataModel
import com.mutualmobile.harvestKmp.domain.model.request.User
import org.koin.androidx.compose.get


@Composable
fun CreateGroupScreen(
    navController: NavHostController,
    agVm: AddGroupViewModel = get(),
    userState: PraxisDataModel.DataState,
    _participants: String?,
) {

    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    println("CREATE GROUP SCREEN with ${agVm.participants}")
    val participants = agVm.participants

    LaunchedEffect(agVm.currentNavigationCommand) {
        when (agVm.currentNavigationCommand) {
            is NavigationPraxisCommand -> {
                val destination = (agVm.currentNavigationCommand as NavigationPraxisCommand).screen
                agVm.resetAll {
                    navController clearBackStackAndNavigateTo destination
                }
            }
        }
    }

    LaunchedEffect(userState) {
        when (userState) {
            is PraxisDataModel.SuccessState<*> -> {
                agVm.getGroupParticipants(userState)
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
                title = { Text("Create Group") },
                backgroundColor = MaterialTheme.colors.primary,
                contentPadding = WindowInsets.statusBars.asPaddingValues()
            )
        },
        floatingActionButton = { CreateGroupButtonCompose(agVm) },
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

                    //LoadingIndicator(loadingFlow = agVm.loading)

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(Modifier.weight(1f)) {
                            GreateGroupTextField(
                                value = agVm.currentGroupName,
                                onValueChange = { updatedString -> agVm.currentGroupName = updatedString },
                                placeholderText = "GROUP NAME"
                            )
                            ParticipantList(participants = participants, addGroupViewModel = agVm, context = context)
                        }
                    }

                }
            }
        }
    }

}

@Composable
fun ParticipantList(participants: List<User>, addGroupViewModel: AddGroupViewModel, context: Context) {

    val currentUser = addGroupViewModel.currentUser.collectAsState()

    if(participants.isNotEmpty()) {
        Text(
            text = "PARTICIPANT LIST",
            modifier = Modifier
                .padding(top = 106.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(top = 145.dp, start = 20.dp, end = 20.dp),
                //.padding(start = 4.dp, end = 4.dp),
            //verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(participants) { position, contact ->

                    UserCard(
                        position = position,
                        otherUser = contact,
                        currentUser = currentUser.value,
                        isSelected = true,
                        onChatClicked = {
                            Toast.makeText(
                                context,
                                contact.email + " selected..",
                                Toast.LENGTH_SHORT
                            ).show()
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

@Composable
fun CreateGroupButtonCompose(addGroupViewModel: AddGroupViewModel){

    FloatingActionButton(
        shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 40)),
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        onClick = { addGroupViewModel.createGroupWithParticipant() }
    ) {
        Icon(Icons.Default.ArrowForward, contentDescription = null)
    }
}