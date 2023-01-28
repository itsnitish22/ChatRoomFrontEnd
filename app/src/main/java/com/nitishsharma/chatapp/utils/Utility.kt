package com.nitishsharma.chatapp.utils

import com.nitishsharma.chatapp.BuildConfig

object Utility {
    const val SERVER_PATH = "wss://chatappbackendws.azurewebsites.net/"
    const val SERVER_PATH_PIESOCKET_TEST =
        "wss://${BuildConfig.PIESOCKET_CLUSTER_ID}.piesocket.com/v3/${BuildConfig.PIESOCKET_ROOM_ID}?api_key=${BuildConfig.PIESOCKET_API_KEY}&notify_self=1"
}