package com.mutualmobile.harvestKmp.android.ui.screens.callScreen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import co.daily.CallClient
import co.daily.CallClientListener
import co.daily.model.Participant
import co.daily.model.ParticipantId
import co.daily.view.VideoView


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CallScreen(
    navController: NavHostController,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val dailyClient = CallClient(LocalContext.current)
    var cameraEnabled by remember { mutableStateOf(true) }
    var microphoneEnabled by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Video Call") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ToggleButton(
                        checked = cameraEnabled,
                        onCheckedChange = {
                            cameraEnabled = it
                            dailyClient.setInputsEnabled(it)
                        },
                        textOn = "Cam On",
                        textOff = "Cam Off"
                    )

                    ToggleButton(
                        checked = microphoneEnabled,
                        onCheckedChange = {
                            microphoneEnabled = it
                            dailyClient.setInputsEnabled(it)
                        },
                        textOn = "Unmuted",
                        textOff = "Muted"
                    )

                    Button(onClick = {
                        dailyClient.leave()
                        navController.navigateUp()
                    }) {
                        Text("Leave Call")
                    }
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    factory = { context ->
                        VideoView(context).apply {
                            val call = CallClient(LocalContext.current)

                            val videoViews = mutableMapOf<ParticipantId, VideoView>()

                            val layout = scaffoldState.drawerState

                            // Listen for events
                            call.addListener(object : CallClientListener {

                                // Handle a remote participant joining
                                override fun onParticipantJoined(participant: Participant) {
                            val participantView = layoutInflater.inflate(R.layout.participant_view, layout, false)

                                    val videoView = participantView.findViewById<VideoView>(R.id.participant_video)
                                    videoView.track = participant.media?.camera?.track
                                    videoViews[participant.id] = videoView

                                    layout.addView(participantView)
                                }

                                // Handle a participant updating (e.g. their tracks changing)
                                override fun onParticipantUpdated(participant: Participant) {
                                    val videoView = videoViews[participant.id]
                                    videoView?.track = participant.media?.camera?.track
                                }

                                override fun onInputsUpdated(inputSettings: InputSettings) {
                                    toggleCamera.isChecked = inputSettings.camera.isEnabled
                                    toggleMicrophone.isChecked = inputSettings.microphone.isEnabled
                                }

                                toggleMicrophone.setOnCheckedChangeListener { _, isChecked ->
                                    Log.d("BUTTONS", "User tapped the Mute button")
                                    call.setInputEnabled(OutboundMediaType.Microphone, isChecked)
                                }

                                toggleCamera.setOnCheckedChangeListener { _, isChecked ->
                                    Log.d("BUTTONS", "User tapped the Cam button")
                                    call.setInputEnabled(OutboundMediaType.Camera, isChecked)
                                }

                                findViewById<Button>(R.id.leave)
                                .setOnClickListener {
                                    Log.d("BUTTONS", "User tapped the Leave button")
                                    call.leave {
                                        it.error?.apply {
                                            Log.e(TAG, "Got error while leaving call: $msg")
                                        } ?: Log.d(TAG, "Successfully left call")
                                    }
                                }
                            })

                            call.join(url = "[YOUR_DAILY_ROOM_URL]") {
                                it.error?.apply {
                                    Log.e(TAG, "Got error while joining call: $msg")
                                }
                                it.success?.apply {
                                    Log.i(TAG, "Successfully joined call.")
                                    toggleCamera.isChecked = call.inputs().camera.isEnabled
                                    toggleMicrophone.isChecked = call.inputs().microphone.isEnabled
                                }
                            }
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Black)
                ) {
                    Text(
                        text = "Video Stream",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    )
}

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    textOn: String,
    textOff: String
) {
    Button(onClick = { onCheckedChange(!checked) }) {
        Text(if (checked) textOn else textOff)
    }
}
