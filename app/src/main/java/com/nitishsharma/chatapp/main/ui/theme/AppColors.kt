package com.nitishsharma.chatapp.main.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

class AppColors(
    appBackgroundLightGray: Color,
    appBackgroundDarkGray: Color,
    chatRoomLightGreen: Color,
    chatRoomLightBlue: Color,
    neutral1: Color,
    neutral2: Color,
    neutral3: Color,
    neutral4: Color,
    neutral5: Color,
    whitishWhite: Color
) {
    var appBackgroundLightGray by mutableStateOf(appBackgroundLightGray)
        private set
    var appBackgroundDarkGray by mutableStateOf(appBackgroundDarkGray)
        private set
    var chatRoomLightGreen by mutableStateOf(chatRoomLightGreen)
        private set
    var chatRoomLightBlue by mutableStateOf(chatRoomLightBlue)
        private set
    var neutral1 by mutableStateOf(neutral1)
        internal set
    var neutral2 by mutableStateOf(neutral2)
        private set
    var neutral3 by mutableStateOf(neutral3)
        private set
    var neutral4 by mutableStateOf(neutral4)
        private set
    var neutral5 by mutableStateOf(neutral5)
        private set
    var whitishWhite by mutableStateOf(neutral5)
        private set

    fun copy(): AppColors = AppColors(
        appBackgroundLightGray,
        appBackgroundDarkGray,
        chatRoomLightGreen,
        chatRoomLightBlue,
        neutral1,
        neutral2,
        neutral3,
        neutral4,
        neutral5,
        whitishWhite
    )

    fun updateColorsFrom(other: AppColors) {
        appBackgroundLightGray = other.appBackgroundLightGray
        appBackgroundDarkGray = other.appBackgroundDarkGray
        chatRoomLightGreen = other.chatRoomLightGreen
        chatRoomLightBlue = other.chatRoomLightBlue
        neutral1 = other.neutral1
        neutral2 = other.neutral2
        neutral3 = other.neutral3
        neutral4 = other.neutral4
        neutral5 = other.neutral5
        whitishWhite = other.whitishWhite
    }
}

fun lightColors(): AppColors = AppColors(
    appBackgroundLightGray = AppBackgroundLightGray,
    appBackgroundDarkGray = AppBackgroundDarkGray,
    chatRoomLightGreen = ChatRoomLightGreen,
    chatRoomLightBlue = ChatRoomLightBlue,
    neutral1 = Neutral1,
    neutral2 = Neutral2,
    neutral3 = Neutral3,
    neutral4 = Neutral4,
    neutral5 = Neutral5,
    whitishWhite = WhitishWhite
)

internal val LocalColors = staticCompositionLocalOf { lightColors() }
