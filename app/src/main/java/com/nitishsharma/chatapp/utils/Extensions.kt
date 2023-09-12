package com.nitishsharma.chatapp.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nitishsharma.chatapp.base.common.model.LoadingModel

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

fun Context.setStatusBarColor(activity: Activity, colorResId: Int) {
    val window = activity.window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, colorResId)
}

fun Fragment.setStatusBarColor(activity: Activity, colorResId: Int) {
    requireContext().setStatusBarColor(activity, colorResId)
}

fun View.setVisibilityBasedOnLoadingModel(loadingModel: LoadingModel) {
    visibility = when (loadingModel) {
        LoadingModel.LOADING -> View.VISIBLE
        else -> View.GONE
    }
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}