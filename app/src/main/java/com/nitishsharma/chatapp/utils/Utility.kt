package com.nitishsharma.chatapp.utils

import android.os.Bundle
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.google.gson.Gson
import com.nitishsharma.domain.api.models.chatresponse.parseMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

object Utility {
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

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun getCurrentNetworkIPAddress(): String? {
        val interfaces = NetworkInterface.getNetworkInterfaces()
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            val addresses = networkInterface.inetAddresses
            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()
                if (!address.isLoopbackAddress && address is InetAddress) {
                    val ipv4Address = address.hostAddress
                    val isIPv4 = ipv4Address.indexOf(':') < 0
                    if (isIPv4) {
                        return ipv4Address
                    }
                }
            }
        }
        return ""
    }

    fun formatDateTime(dateTime: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM, yyyy hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(dateTime)
        return date?.let { outputFormat.format(it) }
    }

    fun getCurrentTimeStamp(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("d MMM, yyyy h:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    enum class ButtonState { Pressed, Idle }

    fun Modifier.bounceClick() = composed {
        var buttonState by remember { mutableStateOf(ButtonState.Idle) }
        val scale by animateFloatAsState(if (buttonState == ButtonState.Pressed) 0.90f else 1f)

        this
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { }
            )
            .pointerInput(buttonState) {
                awaitPointerEventScope {
                    buttonState = if (buttonState == ButtonState.Pressed) {
                        waitForUpOrCancellation()
                        ButtonState.Idle
                    } else {
                        awaitFirstDown(false)
                        ButtonState.Pressed
                    }
                }
            }
    }
}