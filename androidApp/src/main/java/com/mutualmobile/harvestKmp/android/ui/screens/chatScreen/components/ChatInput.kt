package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mutualmobile.harvestKmp.android.ui.utils.HashUtils
import com.mutualmobile.harvestKmp.android.ui.utils.ImagePicker
import com.mutualmobile.harvestKmp.android.ui.utils.toComposeImageBitmap
import com.mutualmobile.harvestKmp.domain.model.TextType
import fileFromContentUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest

@Composable
internal fun ChatInput(modifier: Modifier = Modifier, onMessageChange: (String, type: TextType, imageBytes: ByteArray?, imageCheckSum: String?) -> Unit) {

    var input by remember { mutableStateOf(TextFieldValue("")) }
    val textEmpty: Boolean by derivedStateOf { input.text.isEmpty() }

    val context = LocalContext.current
    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {

        ChatTextField(
            modifier = modifier.weight(1f),
            input = input,
            empty = textEmpty,
//            selectedImage = selectedImage,
//            selectedImageChecksum = selectedImageChecksum,
//            selectedImageBitmap = selectedImageBitmap,
//            photoUri = photoUri,
           context = context,
            onValueChange = {
                input = it
            }
        )

        Spacer(modifier = Modifier.width(6.dp))

        FloatingActionButton(
            modifier = Modifier.size(48.dp),
            //backgroundColor = Color(0xff00897B),
            backgroundColor = Color(0xFF0292FA),
            onClick = {
                if (!textEmpty) {
                    onMessageChange(input.text, TextType.TEXT, null, null)
                    input = TextFieldValue("")
                }
            }
        ) {
            Icon(
                tint = Color.White,
                imageVector = if (textEmpty) Icons.Filled.Mic else Icons.Filled.Send,
                contentDescription = null
            )
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ChatTextField(
    modifier: Modifier = Modifier,
    input: TextFieldValue,
    empty: Boolean,
//    selectedImage: ByteArray?,
//    selectedImageChecksum: String,
//    selectedImageBitmap: ImageBitmap?,
//    photoUri: Uri?,
    context: Context,
    onValueChange: (TextFieldValue) -> Unit
) {


    var showImagePicker by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {

            println("PHOTO URL $uri")
            val contentResolver = context.contentResolver
        }
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colors.surface,
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(2.dp),
            verticalAlignment = Alignment.Bottom
        ) {

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {

                IndicatingIconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.then(Modifier.size(circleButtonSize)),
                    indication = rememberRipple(bounded = false, radius = circleButtonSize / 2)
                ) {
                    Icon(imageVector = Icons.Default.Mood, contentDescription = "emoji")
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = circleButtonSize),
                    contentAlignment = Alignment.CenterStart
                ) {

                    BasicTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        textStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        value = input,
                        onValueChange = onValueChange,
                        cursorBrush = SolidColor(Color(0xff00897B)),
                        decorationBox = { innerTextField ->
                            if (empty) {
                                Text("Message", fontSize = 18.sp)
                            }
                            innerTextField()
                        }
                    )
                }

                IndicatingIconButton(
                    onClick = {
                        showImagePicker = true
                        launcher.launch(
                            PickVisualMediaRequest(
                                mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
                            )
                        )
                    },
                    modifier = Modifier.then(Modifier.size(circleButtonSize)),
                    indication = rememberRipple(bounded = false, radius = circleButtonSize / 2)
                ) {
                    Icon(
                        modifier = Modifier.rotate(-45f),
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "attach"
                    )
                }
                AnimatedVisibility(visible = empty) {
                    IndicatingIconButton(
                        onClick = {

                        },
                        modifier = Modifier.then(Modifier.size(circleButtonSize)),
                        indication = rememberRipple(bounded = false, radius = circleButtonSize / 2)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "camera"
                        )
                    }
                }
            }
        }
    }
}

val circleButtonSize = 48.dp