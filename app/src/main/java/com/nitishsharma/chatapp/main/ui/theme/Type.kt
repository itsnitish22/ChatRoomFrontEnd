package com.nitishsharma.chatapp.main.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nitishsharma.chatapp.R

private val sansRegular = FontFamily(
    Font(R.font.sans_regular, FontWeight.W400)
)

private val sansBold = FontFamily(
    Font(R.font.sans_bold, FontWeight.W700)
)

private val sansMedium = FontFamily(
    Font(R.font.sans_med, FontWeight.W700)
)

data class AppTypography(
    val r1: TextStyle = TextStyle(
        fontFamily = sansRegular,
        fontSize = 10.sp,
        lineHeight = 12.sp
    ),
    val r2: TextStyle = TextStyle(
        fontFamily = sansRegular,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    val r3: TextStyle = TextStyle(
        fontFamily = sansRegular,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    val b1: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 10.sp,
        lineHeight = 12.sp
    ),
    val b2: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    val b3: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    val b4: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 21.sp,
        lineHeight = 32.sp
    ),
    val b5: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    val b6: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 28.sp,
        lineHeight = 40.sp
    ),
    val b7: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    val b8: TextStyle = TextStyle(
        fontFamily = sansBold,
        fontSize = 40.sp,
        lineHeight = 56.sp
    ),
    val titleB1: TextStyle = TextStyle(
        fontFamily = sansMedium,
        fontSize = 24.sp,
        lineHeight = 24.sp
    )
)

internal val LocalTypography = staticCompositionLocalOf { AppTypography() }
