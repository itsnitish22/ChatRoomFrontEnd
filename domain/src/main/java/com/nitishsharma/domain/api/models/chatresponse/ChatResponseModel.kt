package com.nitishsharma.domain.api.models.chatresponse

import org.json.JSONObject

data class Message(
    val userName: String,
    val message: String,
    val roomId: String,
    val isSent: Boolean
)

fun parseMessage(json: String): Message {
    val nameValuePairs = JSONObject(json).getJSONObject("nameValuePairs")
    return Message(
        userName = nameValuePairs.getString("userName"),
        message = nameValuePairs.getString("message"),
        roomId = nameValuePairs.getString("roomId"),
        isSent = nameValuePairs.getBoolean("isSent")
    )
}