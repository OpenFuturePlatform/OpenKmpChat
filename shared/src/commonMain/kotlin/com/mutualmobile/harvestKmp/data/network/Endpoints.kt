package com.mutualmobile.harvestKmp.data.network

object Endpoint {
    private const val HOST_URL = "http://192.168.0.102" // WIFI
    //private const val HOST_URL = "http://192.168.140.204" // MOBILE
    const val SPRING_BOOT_BASE_URL = "$HOST_URL:5001"
    const val STATE_URL = "$HOST_URL:8545"

    private const val API_URL = "/api/v1"
    private const val ADMIN = "/admin"
    private const val ORG_ADMIN = "/org-admin"
    const val UN_AUTH_API = "$API_URL/public"
    const val ORGANIZATIONS = "$UN_AUTH_API/organizations"
    const val UN_AUTH_ORGANISATION = "$UN_AUTH_API/organization"

    const val SIGNUP = "$UN_AUTH_API/signup"
    const val LOGIN = "$UN_AUTH_API/login"
    const val FORGOT_PASSWORD = "$UN_AUTH_API/forgotPassword"
    const val RESET_PASSWORD_ENDPOINT = "$UN_AUTH_API/resetPassword"
    const val FCM_TOKEN = "$API_URL/token"
    const val CHANGE_PASSWORD = "$API_URL/changePassword"
    const val LOGOUT = "$API_URL/logout"
    const val REFRESH_TOKEN = "$API_URL/refreshToken"

    const val USER = "$API_URL/user"
    private const val USER_PROJECT = "$USER/project"
    const val USER_ASSIGNED_PROJECTS = "$USER/assigned-projects"

    const val NOTIFICATIONS = "$API_URL/notifications"
    const val NOTIFICATION_COUNT = "$API_URL/notificationCount"

    const val ORGANIZATION = "$API_URL/organization"
    const val ORG_USERS = "$API_URL/organization/users"
    const val ORG_PROJECT = "$ORGANIZATION/project"
    const val PROJECTS = "$API_URL/projects"
    const val FIND_ORGANIZATION = "$ORGANIZATION/find-organization"

    const val LIST_USERS_IN_PROJECT = "$ORG_PROJECT/list-users"

    //ADMIN
    const val LIST_USERS = "$API_URL$ADMIN/users"
    const val ASSIGN_PROJECT = "$API_URL$ORG_ADMIN/assign-user-project"

    const val LOG_WORK = "$USER_PROJECT/log-work"
    const val GET_LOG_WORK = "$USER_PROJECT/get-work-log"

    const val CHAT_URL = "$API_URL/messages"
    const val CHATGPT_URL = "$API_URL/messages/assistant"
    const val ASSISTANT_URL = "$API_URL/ai"
    const val WALLET_URL = "$API_URL/wallets"
    const val TASK_URL = "$API_URL/tasks"
    const val GROUP_URL = "$API_URL/groups"
    const val ATTACHMENT_URL = "$API_URL/attachments"
    const val LIST_RECIPIENT_CHATS = "$CHAT_URL/recipient/"
    const val LIST_CHATS = "$CHAT_URL/chat/"
    const val LIST_CONTACTS = "$API_URL/user/all"
    const val USER_DETAIL = "$API_URL/user/userDetails"
    const val LIST_RECIPIENT_GROUP = "$CHAT_URL/front-messages?user="

    const val RATES_URL = "/api/currency/rate"
    const val BALANCE_URL = "/api/wallets/v2/balance"
    const val TRANSACTIONS_URL = "/api/wallets/v2/transactions"
    const val GAS_PRICE_URL = "/api/wallets/v2/gas-price"
    const val GAS_LIMIT_URL = "/api/wallets/v2/gas-limit"
    const val NONCE_URL = "/api/wallets/v2/nonce"
    const val BROADCAST_URL = "/api/wallets/v2/broadcast"
    const val CONTRACT_URL = "/api/wallets/contracts"

    object Params {
        const val START_DATE = "startDate"
        const val END_DATE = "endDate"
        const val EMAIL = "email"
        const val TOKEN = "token"
        const val PASSWORD = "password"
        const val FILE = "file"

        const val OFFSET: String = "offset"
        const val LIMIT: String = "limit"
        const val TYPE: String = "type"
        const val STATUS: String = "status"
        const val USER_ID: String = "userId"

        const val ID: String = "id"

        //filtering
        const val SEARCH_KEY = "search"

        //sorting
        const val SORT_BY = "sortBy"
        const val SORT_ORDER = "sortOrder"

        const val NOTIFICATION_ID = "notificationId"

        const val ORG_IDENTIFIER = "identifier"

        //private chat
        const val SENDER_ID = "senderId"
        const val RECIPIENT_ID = "recipientId"
        //websocket
        const val SOCKET_URL = "ws://$SPRING_BOOT_BASE_URL/ws"
        const val CHAT_TOPIC = "/topic/chat"
        const val CHAT_LINK_SOCKET = "/app/direct-message"
    }
}
