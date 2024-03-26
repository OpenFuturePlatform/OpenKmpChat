package com.mutualmobile.harvestKmp.domain.model
import dev.icerock.moko.graphics.Color
import dev.icerock.moko.resources.ImageResource
import kotlinx.datetime.Clock
import kotlin.random.Random
import kotlin.random.nextInt

data class Message(
    val user: ChatUser,
    val text: String,
    val seconds: Long,
    val id: Long
) {
    constructor(
        user: ChatUser,
        text: String
    ) : this(
        user = user,
        text = text,
        seconds = Clock.System.now().epochSeconds,
        id = Random.nextLong()
    )
}

data class ChatUser(
    val id: String,
    val name: String,
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