import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mutualmobile.harvestKmp.android.ui.theme.Dimens
import com.mutualmobile.harvestKmp.android.ui.utils.HashUtils
import com.mutualmobile.harvestKmp.android.ui.utils.ImagePicker
import com.mutualmobile.harvestKmp.android.ui.utils.toComposeImageBitmap
import com.mutualmobile.harvestKmp.domain.model.TextType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.security.MessageDigest

@Composable
fun SendMessage(sendMessage: (prompt: String, type: TextType, imageBytes: ByteArray?, imageCheckSum: String?) -> Unit) {

    var inputText by remember { mutableStateOf("") }
    var imageCaptionText by remember { mutableStateOf("") }
    var selectedImage by remember { mutableStateOf<ByteArray?>(null) }
    var selectedImageChecksum by remember { mutableStateOf("") }
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
                input.readBytes()
            }

            selectedImageChecksum = HashUtils.getCheckSumFromFile(
                MessageDigest.getInstance("MD5"),
                fileFromContentUri(context, uri)
            )
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (photoUri != null) {
            Box(Modifier.fillMaxHeight().padding(vertical = 4.dp, horizontal = 16.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(data = photoUri)
                            .build()
                    ),
                    contentDescription = "Selected image",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background)
                        //.imePadding()
                        //.wrapContentHeight(),
                            ,
                    colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.White),
                    value = imageCaptionText,
                    placeholder = {
                        Text("Add caption...")
                    },
                    keyboardOptions = KeyboardOptions.Default,
                    onValueChange = {
                        imageCaptionText = it
                    },
                    //shape = RoundedCornerShape(Dimens.XS),
                    leadingIcon = {
                        IconButton(onClick = {
                            photoUri = null
                            imageCaptionText = ""
                            selectedImage = null
                            photoUri = null
                        }) {
                            Icon(Icons.Rounded.Delete, "Delete Image File")
                        }
                    },
                    trailingIcon = {
                        IconButton(
                                onClick =  {
                                        sendMessage(imageCaptionText, TextType.ATTACHMENT, selectedImage!!, selectedImageChecksum)
                                        imageCaptionText = ""
                                        selectedImage = null
                                        photoUri = null
                                    },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = MaterialTheme.colors.primary
                                )
                            }

                    }
                )
//                IconButton(
//                    onClick = { photoUri = null },
//                    modifier = Modifier.size(48.dp).align(Alignment.BottomStart),
//                ) {
//                    Icon(Icons.Rounded.Delete, "Remove attached Image File")
//                }
//                IconButton(
//                    onClick = {
//                        sendMessage(photoUri.toString(), TextType.ATTACHMENT, selectedImage!!)
//                    },
//                    modifier = Modifier.size(48.dp).align(Alignment.BottomEnd),
//                ) {
//                    Icon(Icons.Rounded.Send, "Send Image File")
//                }
            }
        } else {

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
                                    sendMessage(inputText, TextType.TEXT, null, null)
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

fun fileFromContentUri(context: Context, contentUri: Uri): File {
    // Preparing Temp file name
    val fileExtension = getFileExtension(context, contentUri)
    val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

    // Creating Temp file
    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()

    try {
        val oStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)

        inputStream?.let {
            copy(inputStream, oStream)
        }

        oStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return tempFile
}

private fun getFileExtension(context: Context, uri: Uri): String? {
    val fileType: String? = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

@Composable
fun LinearProgressIndicator(
    /*@FloatRange(from = 0.0, to = 1.0)*/
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = color.copy(alpha = IndicatorBackgroundOpacity)
)