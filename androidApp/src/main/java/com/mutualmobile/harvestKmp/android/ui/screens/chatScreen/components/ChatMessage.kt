import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components.ChatColors
import com.mutualmobile.harvestKmp.domain.model.Message
import com.mutualmobile.harvestKmp.domain.model.TextType

@Composable
fun Triangle(risingToTheRight: Boolean, background: Color) {
    Box(
        Modifier
            .padding(bottom = 10.dp, start = 0.dp)
            .clip(TriangleEdgeShape(risingToTheRight))
            .background(background)
            .size(6.dp)
    )
}

@Composable
fun ChatMessage(isMyMessage: Boolean, message: Message) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {

        Row(verticalAlignment = Alignment.Bottom) {
            if (!isMyMessage) {
//                Column {
//                    UserPic(message.user)
//                }
                Spacer(Modifier.size(2.dp))
                Column {
                    Triangle(true, ChatColors.OTHERS_MESSAGE)
                }
            }

            Column {
                Box(
                    Modifier.clip(
                        RoundedCornerShape(
                            10.dp,
                            10.dp,
                            if (!isMyMessage) 10.dp else 0.dp,
                            if (!isMyMessage) 0.dp else 10.dp
                        )
                    )
                        .background(color = if (!isMyMessage) ChatColors.OTHERS_MESSAGE else ChatColors.MY_MESSAGE)
                        .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
                ) {
                    Column {
                        if(!isMyMessage) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = message.user.name,
                                    style = MaterialTheme.typography.body1.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.sp,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.size(3.dp))
                        if (message.type == TextType.TEXT){
                            Text(
                                text = message.text,
                                style = MaterialTheme.typography.body1.copy(
                                    fontSize = 18.sp,
                                    letterSpacing = 0.sp
                                )
                            )
                        } else {
                            if (message.text.isNotEmpty()) {
                                Box(Modifier.size(96.dp).padding(vertical = 4.dp, horizontal = 16.dp)) {

                                    println("Message: ${message.attachmentUrl}")
                                    val painter = rememberAsyncImagePainter(
                                        ImageRequest
                                            .Builder(LocalContext.current)
                                            .data(data = message.attachmentUrl)
                                            .build()
                                    )
                                    Image(
                                        painter = painter,
                                        contentDescription = message.text,
                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop,
                                    )
//                                    AsyncImage(
//                                        model = ImageRequest.Builder(LocalContext.current)
//                                            .data("https://mercury.bid.cars/0-30821485/2016-Toyota-Highlander-5TDJKRFH4GS332367-2.jpg")
//                                            .build(),
//                                        contentDescription = "ImageRequest example",
//                                    )
//                                    Text(
//                                        text = message.text,
//                                        style = MaterialTheme.typography.body1.copy(
//                                            fontSize = 18.sp,
//                                            letterSpacing = 0.sp
//                                        )
//                                    )
                                }
                            }
                        }
                        Spacer(Modifier.size(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(
                                text = timeToString(message.seconds),
                                textAlign = TextAlign.End,
                                style = MaterialTheme.typography.subtitle1.copy(fontSize = 10.sp),
                                color = ChatColors.TIME_TEXT
                            )
                        }
                    }
                }
                Box(Modifier.size(10.dp))
            }
            if(isMyMessage) {
                Column {
                    Triangle(false, ChatColors.MY_MESSAGE)
                }
            }
        }
    }
}



class TriangleEdgeShape(val risingToTheRight: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val trianglePath = if(risingToTheRight) {
            Path().apply {
                moveTo(x = 0f, y = size.height)
                lineTo(x = size.width, y = 0f)
                lineTo(x = size.width, y = size.height)
            }
        } else {
            Path().apply {
                moveTo(x = 0f, y = 0f)
                lineTo(x = size.width, y = size.height)
                lineTo(x = 0f, y = size.height)
            }
        }

        return Outline.Generic(path = trianglePath)
    }
}

val dotSize = 10.dp // made it bigger for demo
val delayUnit = 300 // you can change delay to change animation speed

//@Composable
//fun DotsPulsing() {
//
//    @Composable
//    fun Dot(
//        scale: Float
//    ) = Spacer(
//        Modifier
//            .size(dotSize)
//            .scale(scale)
//            .background(
//                color = MaterialTheme.colors.primary,
//                shape = CircleShape
//            )
//    )
//
//    val infiniteTransition = rememberInfiniteTransition()
//
//    @Composable
//    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 0f,
//        animationSpec = infiniteRepeatable(
//            animation = keyframes {
//                durationMillis = delayUnit * 4
//                0f at delay with LinearEasing
//                1f at delay + delayUnit with LinearEasing
//                0f at delay + delayUnit * 2
//            }
//        )
//    )
//
//    val scale1 by animateScaleWithDelay(0)
//    val scale2 by animateScaleWithDelay(delayUnit)
//    val scale3 by animateScaleWithDelay(delayUnit * 2)
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        val spaceSize = 2.dp
//
//        Dot(scale1)
//        Spacer(Modifier.width(spaceSize))
//        Dot(scale2)
//        Spacer(Modifier.width(spaceSize))
//        Dot(scale3)
//    }
//}