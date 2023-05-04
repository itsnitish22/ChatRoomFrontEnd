package com.nitishsharma.chatapp.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.nitishsharma.domain.api.models.chatresponse.parseMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.InetAddress
import java.net.NetworkInterface
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

    fun Context.toast(message: String) {
        Toast.makeText(
            this, message,
            if (message.length <= 25) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        ).show()
    }

    fun Fragment.toast(msg: String) {
        requireContext().toast(msg)
    }

    fun Context.copyTextToClipboard(textToCopy: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, textToCopy)
        clipboard.setPrimaryClip(clip)
    }

    fun Fragment.copyTextToClipboard(textToCopy: String, label: String) {
        requireContext().copyTextToClipboard(textToCopy, label)
    }

    fun Context.shareRoom(roomID: String, roomName: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/html"
            putExtra(
                Intent.EXTRA_TEXT,
                "Join my Room: $roomName\nusing\nRoomID: $roomID"
            )
        }
        startActivity(Intent.createChooser(shareIntent, "Share RoomID using"))
    }

    fun Fragment.shareRoom(roomId: String, roomName: String) {
        requireContext().shareRoom(roomId, roomName)
    }
}