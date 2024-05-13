import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.InputStreamRequestBody
import com.mutualmobile.harvestKmp.android.ui.theme.Dimens
import com.mutualmobile.harvestKmp.android.ui.theme.PrimaryLightColor
import com.mutualmobile.harvestKmp.android.ui.utils.ImagePicker
import com.mutualmobile.harvestKmp.android.ui.utils.URIPathHelper
import com.mutualmobile.harvestKmp.android.ui.utils.toComposeImageBitmap
import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

@Composable
fun SendMessage(sendMessage: (prompt: String, type: TextType, imageBytes: ByteArray?) -> Unit) {

    var inputText by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var showImagePicker by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            photoUri = uri
            println("PHOTO URL $uri")
            val contentResolver = context.contentResolver

            selectedImage = contentResolver.openInputStream(uri)!!.use { input ->
                input.readBytes() // not good for RAM
            }

            sendMessage(uri.toString(), TextType.ATTACHMENT, selectedImage!!)
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
//        if (photoUri != null) {
//            Box(Modifier.size(96.dp).padding(vertical = 4.dp, horizontal = 16.dp)) {
//                val painter = rememberAsyncImagePainter(
//                    ImageRequest
//                        .Builder(LocalContext.current)
//                        .data(data = photoUri)
//                        .build()
//                )
//                Image(
//                    painter = painter,
//                    contentDescription = "Selected image",
//                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop,
//                )
//                IconButton(
//                    onClick = { photoUri = null },
//                    modifier = Modifier.size(48.dp).align(Alignment.TopEnd),
//                ) {
//                    Icon(Icons.Rounded.Delete, "Remove attached Image File")
//                }
//            }
//        }

        TextField(
            label = { Text("Label") },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .imePadding()
                .wrapContentHeight(),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
            value = inputText,
            placeholder = {
                Text("Type message...")
            },
            keyboardOptions = KeyboardOptions.Default,
            onValueChange = {
                inputText = it
            },
            shape = RoundedCornerShape(Dimens.XS),
            leadingIcon = {
                IconButton(onClick = {
                    showImagePicker = true
                    launcher.launch(
                        PickVisualMediaRequest(
                            mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo
                        )
                    )
                }) {
                    Icon(Icons.Rounded.Add, "Attach Image File")
                }
            },
            trailingIcon = {
                if (inputText.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .imePadding()
                            .clickable {
                                sendMessage(inputText, TextType.TEXT, null)
                                inputText = ""
                                selectedImage = null
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }
        )
    }

    if (showImagePicker) {
        ImagePicker {
            selectedImage = it
            showImagePicker = false
        }
    }

    LaunchedEffect(selectedImage) {
        withContext(Dispatchers.Default) {
            selectedImageBitmap = selectedImage?.toComposeImageBitmap()
        }
    }
}