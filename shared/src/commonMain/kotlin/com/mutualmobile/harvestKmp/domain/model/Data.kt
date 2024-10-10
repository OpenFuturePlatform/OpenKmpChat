package com.mutualmobile.harvestKmp.domain.model
import com.mutualmobile.harvestKmp.domain.model.request.BlockchainType
import com.mutualmobile.harvestKmp.domain.model.response.WalletResponse
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
    var isSent: Boolean,
    val attachmentIds: List<Int>?,
    val seconds: Long,
    val id: Long
) {
    constructor(
        user: ChatUser,
        recipient: String,
        text: String,
        attachmentIds: List<Int>?,
        type: TextType
    ) : this(
        user = user,
        recipient = recipient,
        text = text,
        type = type,
        attachmentIds = attachmentIds,
        isSent = false,
        seconds = Clock.System.now().epochSeconds,
        id = Random.nextLong()
    )
}
data class Wallet(
    val userId: String,
    val address: String,
    val seedPhrase: String,
    val privateKey: String,
    val blockchainType: BlockchainType,
    val seconds: Long,
    val id: Long
){
    constructor(
        userId: String,
        address: String,
        seedPhrase: String,
        privateKey: String,
        blockchainType: BlockchainType,
    ) : this(
        userId = userId,
        address = address,
        privateKey = privateKey,
        blockchainType = blockchainType,
        seedPhrase = seedPhrase,
        seconds = Clock.System.now().epochSeconds,
        id = Random.nextLong()
    )

}

data class Attachment(
    val fileName: String,
    var fileCheckSum: String,
    val captionText: String,
    var attachmentUrl: String,
    var fileByteArray: ByteArray,
    val fileType: String,
    var isSent: Boolean,
    val seconds: Long,
    val id: Long
) {
    constructor(
        fileName: String,
        fileCheckSum: String,
        captionText: String,
        attachmentUrl: String,
        fileByteArray: ByteArray,
        fileType: String,
        isSent: Boolean
    ) : this(
        fileName = fileName,
        fileCheckSum = fileCheckSum,
        captionText = captionText,
        fileByteArray = fileByteArray,
        fileType = fileType,
        attachmentUrl = attachmentUrl,
        isSent = isSent,
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
data class BlockchainWallets(
    val blockchainType: BlockchainType,
    val blockchainIcon: String,
    val wallets: List<WalletResponse>
)
