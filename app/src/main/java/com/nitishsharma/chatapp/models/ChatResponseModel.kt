package com.nitishsharma.chatapp.models

import org.json.JSONObject

data class Message(
    val name: String,
    val message: String,
    val roomid: String,
    val isSent: Boolean
)

fun parseMessage(json: String): Message {
    val nameValuePairs = JSONObject(json).getJSONObject("nameValuePairs")
    return Message(
        name = nameValuePairs.getString("name"),
        message = nameValuePairs.getString("message"),
        roomid = nameValuePairs.getString("roomid"),
        isSent = nameValuePairs.getBoolean("isSent")
    )
}