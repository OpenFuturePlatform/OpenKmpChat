import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mutualmobile.harvestKmp.android.ui.theme.Dimens
import com.mutualmobile.harvestKmp.android.ui.theme.PrimaryLightColor

@Composable
fun SendMessage(sendMessage: (String) -> Unit) {
    var inputText by remember { mutableStateOf("") }
    var showImagePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
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
                Icon(Icons.Default.Add, "Attach Image File")
            },
            trailingIcon = {
                if (inputText.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .imePadding()
                            .clickable {
                                sendMessage(inputText)
                                inputText = ""
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

@Composable
fun ChatNewBox(
    onSendChatClickListener: (String) -> Unit
) {
    var chatBoxValue by remember { mutableStateOf(TextFieldValue("")) }
    Row(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = chatBoxValue,
            onValueChange = { newText ->
                chatBoxValue = newText
            },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(text = "Type something")
            }
        )
        IconButton(
            onClick = {
                val msg = chatBoxValue.text
                if (msg.isBlank()) return@IconButton
                onSendChatClickListener(chatBoxValue.text)
                chatBoxValue = TextFieldValue("")
            },
            modifier = Modifier
                .clip(CircleShape)
                .background(color = PrimaryLightColor)
                .align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "Send",
                modifier = Modifier.fillMaxSize().padding(8.dp)
            )
        }
    }
}