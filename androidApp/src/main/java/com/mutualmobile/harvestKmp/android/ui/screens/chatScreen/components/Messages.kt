package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components

import ChatMessage
import com.mutualmobile.harvestKmp.domain.model.ChatUser
import com.mutualmobile.harvestKmp.domain.model.Message
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import com.mutualmobile.harvestKmp.domain.model.response.GetUserResponse

@Composable
internal fun Messages(messages: List<Message>, myUser: ChatUser) {

    val listState = rememberLazyListState()
    if (messages.isNotEmpty()) {
        LaunchedEffect(messages.last()) {
            listState.animateScrollToItem(messages.lastIndex, scrollOffset = 2)
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(start = 4.dp, end = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = listState,
    ) {
        item { Spacer(Modifier.size(20.dp)) }
        items(messages, key = { it.id }) {
            ChatMessage(isMyMessage = it.user.email == myUser.email, it)
        }
        item {
            Box(Modifier.height(70.dp))
        }
    }
}



@Composable
fun UserPic(user: ChatUser) {
    val imageSize = 48f
    val painter = user.picture?.let {
        painterResource(it.drawableResId)
    } ?: object : Painter() {
        override val intrinsicSize: Size = Size(imageSize, imageSize)
        override fun DrawScope.onDraw() {
            TODO("Not yet implemented")
        }
    }
    Image(
        modifier = Modifier
            .size(imageSize.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        painter = painter,
        contentDescription = "User picture"
    )
}

@Composable
fun ImageComponent(imageUrl: String, imageLoader: ImageLoader, contentDescription: String) {
    // Use Coil's Image component with rememberAsyncImagePainter for asynchronous image loading
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl, imageLoader = imageLoader),
        contentDescription = contentDescription,
        modifier = Modifier
            .padding(all = 16.dp)
            .size(200.dp)
    )
}