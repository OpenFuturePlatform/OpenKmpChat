package com.mutualmobile.harvestKmp.android.ui.screens.chatScreen.components

import com.mutualmobile.harvestKmp.domain.model.Message

sealed interface Action {
    data class SendMessage(val message: Message) : Action
}

data class State(
    val messages: List<Message> = emptyList()
)

fun chatReducer(state: State, action: Action): State =
    when (action) {
        is Action.SendMessage -> {
            state.copy(
                messages = (state.messages + action.message).takeLast(100)
            )
        }
    }
