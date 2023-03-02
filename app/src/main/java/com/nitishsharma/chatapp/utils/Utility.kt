package com.nitishsharma.chatapp.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.nitishsharma.chatapp.BuildConfig
import com.nitishsharma.chatapp.models.chatresponse.parseMessage
import org.json.JSONArray
import org.json.JSONObject
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


object Utility {
    const val SERVER_PATH = "wss://chatappbackendws.azurewebsites.net/"
    const val SERVER_PATH_PIESOCKET_TEST =
        "wss://${BuildConfig.PIESOCKET_CLUSTER_ID}.piesocket.com/v3/${BuildConfig.PIESOCKET_ROOM_ID}?api_key=${BuildConfig.PIESOCKET_API_KEY}&notify_self=1"

    fun chatMessageResponseMapping(args: Array<Any>?): JSONObject {
        val receivedMessageFromServer =
            parseMessage(JSONArray(Gson().toJson(args))[0].toString())

        val mappedData = JSONObject()
        mappedData.put("userName", receivedMessageFromServer.userName)
        mappedData.put("message", receivedMessageFromServer.message)
        mappedData.put("roomId", receivedMessageFromServer.roomId)
        mappedData.put("isSent", false)

        return mappedData
    }

    fun joinRoomJSONMapping(roomId: String, firebaseInstance: FirebaseAuth): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomId", roomId)
        jsonObject.put("userName", firebaseInstance.currentUser?.displayName)

        return jsonObject
    }

    fun createRoomJSONMapping(userId: String, roomId: String, roomName: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("userId", userId)
        jsonObject.put("roomId", roomId)
        jsonObject.put("roomName", roomName)

        return jsonObject
    }

    fun deleteRoomJSONMapping(roomId: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("roomId", roomId)

        return jsonObject
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
                    val ipAddr = address.getHostAddress()
                    val isIPv4 = ipAddr.indexOf(':') < 0
                    if (isIPv4) {
                        return ipAddr
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