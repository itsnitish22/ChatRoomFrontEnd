package com.nitishsharma.domain.api.utility

import android.os.Bundle
import com.google.gson.Gson
import com.nitishsharma.domain.api.models.chatresponse.parseMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Utils {
    fun bundleToJSONMapping(args: Array<Any>? = null, bundle: Bundle? = null): JSONObject {
        val json = JSONObject()
        val keys = bundle?.keySet()
        if (args.isNullOrEmpty()) {
            keys?.let {
                for (key in keys) {
                    try {
                        json.put(key, bundle.get(key))
                    } catch (e: JSONException) {
                    }
                }
            }
        } else {
            val receivedMessageFromServer =
                parseMessage(JSONArray(Gson().toJson(args))[0].toString())
            json.put("userName", receivedMessageFromServer.userName)
            json.put("message", receivedMessageFromServer.message)
            json.put("roomId", receivedMessageFromServer.roomId)
            json.put("isSent", false)
        }

        return json
    }
}