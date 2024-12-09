package com.mutualmobile.harvestKmp.datamodel

open class OpenCommand

const val BROWSER_SCREEN_ROUTE_SEPARATOR = "/"
const val BROWSER_QUERY = "?"
const val BROWSER_AND = "&"

data class NavigationOpenCommand(val screen: String, val route: String? = null) : OpenCommand()
data class ModalOpenCommand(val title: String, val message: String) : OpenCommand()

object HarvestRoutes {
    object Keys {
        const val orgIdentifier = "orgIdentifier"
        const val orgId = "orgId"
        const val id = "id"
        const val recipient = "recipient"
        const val participants = "participants"
        const val sender = "sender"
        const val chatUid = "chatUid"
        const val isGroup = "isGroup"
        const val profileId = "profileId"
        const val groupId = "groupId"
        const val address = "address"
        const val privateKey = "privateKey"
        const val blockchainType = "blockchainType"
    }

    object Screen {
        const val FORGOT_PASSWORD = "forgot-password"
        const val RESET_PASSWORD = "resetPassword"
        const val CHANGE_PASSWORD = "change-password"
        const val LOGIN = "login"
        const val LOGIN_WITH_ORG_ID_IDENTIFIER = LOGIN
            .plus(BROWSER_QUERY)
            .plus("${Keys.orgId}={${Keys.orgId}}")
            .plus(BROWSER_AND)
            .plus("${Keys.orgIdentifier}={${Keys.orgIdentifier}}")
        const val PIN_INPUT = "pin_input"
        const val PIN_CREATE = "pin_create"
        const val PIN_UNLOCK = "pin_unlock"
        const val SIGNUP = "signup"
        const val NEW_ORG_SIGNUP = "new_org_signup"
        const val ORG_USERS = "users"
        const val ORG_PROJECTS = "projects"
        const val SETTINGS = "settings"
        const val ORG_USER_DASHBOARD = "user-dashboard"
        const val ON_BOARDING = "on_boarding"
        const val FIND_WORKSPACE = "find-workspace"
        const val WORK_ENTRY = "work-entry"
        const val SELECT_WORK_TYPE = "select-work-type"

        const val USER_HOME = "user-home"
        const val USER_WALLETS = "user-wallets"
        const val WALLET_DETAIL = "wallet-detail"
        const val WALLET_SENDER_DETAIL = "wallet-sender-detail"
        const val WALLET_RECEIVER_DETAIL = "wallet-receiver-detail"
        const val CHAT = "chat"
        const val TASK = "task"
        const val CHAT_PRIVATE = "chat-private"
        const val ADD_ACTION = "adds"
        const val ADD_GROUP = "add-group"
        const val ADD_MEMBER = "add-member"
        const val CREATE_GROUP = "create-group"
        const val CONTACT_PROFILE = "contact-profile"
        const val GROUP_PROFILE = "contact-profile"

        const val CHAT_PRIVATE_WITH_SENDER_RECEIVER = CHAT_PRIVATE
            .plus(BROWSER_QUERY)
            .plus("${Keys.sender}={${Keys.sender}}")
            .plus(BROWSER_AND)
            .plus("${Keys.recipient}={${Keys.recipient}}")
            .plus(BROWSER_AND)
            .plus("${Keys.chatUid}={${Keys.chatUid}}")
            .plus(BROWSER_AND)
            .plus("${Keys.isGroup}={${Keys.isGroup}}")


        const val CHAT_GPT = "chat-gpt"

        const val GROUP_WITH_PARTICIPANTS = CREATE_GROUP
            .plus(BROWSER_QUERY)
            .plus("${Keys.participants}={${Keys.participants}}")

        const val ADD_MEMBER_WITH_GROUP_ID = ADD_MEMBER
            .plus(BROWSER_QUERY)
            .plus("${Keys.groupId}={${Keys.groupId}}")

        const val CONTACT_PROFILE_WITH_ID = CONTACT_PROFILE
            .plus(BROWSER_QUERY)
            .plus("${Keys.profileId}={${Keys.profileId}}")

        const val GROUP_PROFILE_WITH_ID = GROUP_PROFILE
            .plus(BROWSER_QUERY)
            .plus("${Keys.profileId}={${Keys.profileId}}")
            .plus(BROWSER_AND)
            .plus("${Keys.isGroup}={${Keys.isGroup}}")

        const val WALLET_DETAIL_WITH_ADDRESS = WALLET_DETAIL
            .plus(BROWSER_QUERY)
            .plus("${Keys.address}={${Keys.address}}")
            .plus(BROWSER_AND)
            .plus("${Keys.blockchainType}={${Keys.blockchainType}}")
            .plus(BROWSER_AND)
            .plus("${Keys.privateKey}={${Keys.privateKey}}")

        const val WALLET_SENDER_DETAIL_WITH_ADDRESS = WALLET_SENDER_DETAIL
            .plus(BROWSER_QUERY)
            .plus("${Keys.address}={${Keys.address}}")
            .plus(BROWSER_AND)
            .plus("${Keys.blockchainType}={${Keys.blockchainType}}")
            .plus(BROWSER_AND)
            .plus("${Keys.privateKey}={${Keys.privateKey}}")

        const val WALLET_RECEIVER_DETAIL_WITH_ADDRESS = WALLET_RECEIVER_DETAIL
            .plus(BROWSER_QUERY)
            .plus("${Keys.address}={${Keys.address}}")
            .plus(BROWSER_AND)
            .plus("${Keys.blockchainType}={${Keys.blockchainType}}")
            .plus(BROWSER_AND)
            .plus("${Keys.privateKey}={${Keys.privateKey}}")

        fun String.withOrgId(identifier: String?, id: String?): String {
            return this.plus(
                BROWSER_QUERY + "${Keys.orgIdentifier}=${identifier ?: ""}" + BROWSER_AND +
                        "${Keys.orgId}=${id ?: ""}"
            )
        }

        fun String.withRecipient(chatUid: String?, isGroup: Boolean?, recipient: String?, sender: String?): String {
            return this.plus(
                BROWSER_QUERY + "${Keys.recipient}=${recipient ?: ""}"
                        + BROWSER_AND + "${Keys.sender}=${sender ?: ""}"
                        + BROWSER_AND + "${Keys.chatUid}=${chatUid ?: ""}"
                        + BROWSER_AND + "${Keys.isGroup}=${isGroup ?: false}"
            )
        }

        fun String.withParticipants(participants: String?): String {
            return this.plus(
                BROWSER_QUERY + "${Keys.participants}=${participants ?: ""}"
            )
        }

        fun String.withDetail(profileId: String?, isGroup: Boolean?): String {
            return this.plus(
                BROWSER_QUERY + "${Keys.profileId}=${profileId ?: ""}" + BROWSER_AND + "${Keys.isGroup}=${isGroup ?: false}"
            )
        }

        fun String.withGroup(groupId: String?): String {
            return this.plus(
                BROWSER_QUERY + "${Keys.groupId}=${groupId ?: ""}"
            )
        }

        fun String.withWalletDetail(address: String?, blockchainType: String?, encryptedPrivateKey: String?): String {
            return this.plus(
                BROWSER_QUERY + "${Keys.address}=${address?: ""}"
                        + BROWSER_AND + "${Keys.blockchainType}=${blockchainType?: ""}"
                        + BROWSER_AND + "${Keys.privateKey}=${encryptedPrivateKey?: ""}"
            )
        }

    }
}