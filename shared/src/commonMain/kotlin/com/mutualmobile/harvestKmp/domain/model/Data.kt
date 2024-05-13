package com.mutualmobile.harvestKmp.domain.model
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.resources.ImageResource
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlin.random.Random
import kotlin.random.nextInt

enum class TextType {
    TEXT, ATTACHMENT
}
data class Message(
    val user: ChatUser,
    val recipient: String,
    val text: String,
    val type: TextType,
    val seconds: Long,
    val id: Long
) {
    constructor(
        user: ChatUser,
        recipient: String,
        text: String,
        type: TextType
    ) : this(
        user = user,
        recipient = recipient,
        text = text,
        type = type,
        seconds = Clock.System.now().epochSeconds,
        id = Random.nextLong()
    )
}

data class AiMessage(
    val sender: String,
    val contentType: TextType,
    val body: String
)

data class GroupDetails(
    val groupId: String,
    val groupName: String,
    val groupCreator: String,
    val groupAvatar: String,
    val participants: List<String>
) {
}

@Serializable
data class MessagesState(
    val message: String? = ""
)

data class ChatUser(
    val id: String,
    val name: String,
    val email: String,
    val color: Color = ColorProvider.getColor(),
    val picture: ImageResource?
)

object ColorProvider {
    val colors = mutableListOf(
        0xFFEA3468,
        0xFFB634EA,
        0xFF349BEA,
    )
    val allColors = colors.toList()
    fun getColor(): Color {
        if(colors.size == 0) {
            colors.addAll(allColors)
        }
        val idx = Random.nextInt(colors.indices)
        val color = colors[idx]
        colors.removeAt(idx)
        return Color(color)
    }
}