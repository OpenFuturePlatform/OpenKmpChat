package com.mutualmobile.harvestKmp.domain.model

import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Similar to [ChatRoom] but stores only the necessary information to display and operate a "chat room card". This
 * object is only LOCAL and stores how the chat room will be displayed on the current device. The same chat room
 * may be displayed differently on different devices.
 */
@Serializable
data class DisplayChatRoom(

    /**
     * The chat rooms UID, used if the user selects the card.
     */
    var chatUid: String = "",

    /**
     * This flag stores if the chat room is a group, or a one-to-one conversation.
     */
    var group: Boolean = false,

    /**
     * Name of the chat room, important for groups.
     */
    var chatRoomName: String = "",

    /**
     * The amount of users in the chat room.
     */
    var memberCount: Int = 0,

    /**
     * Display name of a [User] associated with this chat room. In case of private chats, this will be the non-local
     * other user, in case of groups it is the admin.
     */
    var displayUserName: String = "",

    /**
     * The [Date] of the last message, or null if no message was ever sent. This is user to order groups
     * based on activity.
     */
    var lastMessageTime: LocalDateTime? = null,

    /**
    * The text of the last message, or null if there was no message sent. This is used to show a preview
    * of the chat rooms content.
    */
    var lastMessageText: String? = null,

    /**
     * The image of the chat room. In case of private chats, this is the picture of the other user. For
     * groups, this is the group picture. It can be null, in which case the default pictures will be used.
     */
    var chatRoomPicture: String? = null
)