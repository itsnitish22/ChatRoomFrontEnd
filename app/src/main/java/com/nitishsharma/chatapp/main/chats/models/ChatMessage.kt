package com.nitishsharma.chatapp.main.chats.models

data class ChatMessage(
    val userId: String? = null,
    val userName: String? = null,
    val timeStamp: String? = null,
    val message: String? = null,
    val isSent: Boolean? = null
)
