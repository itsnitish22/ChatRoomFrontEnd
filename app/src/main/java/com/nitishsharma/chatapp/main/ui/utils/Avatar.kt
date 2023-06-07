package com.nitishsharma.chatapp.main.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.nitishsharma.chatapp.main.ui.theme.AppTheme

@Composable
fun HostAvatar(
    avatarSize: Dp,
    avatarUrl: String,
    onClick: () -> Unit = {}
) {
    Avatar(
        modifier = Modifier
            .size(avatarSize),
        avatarUrl = avatarUrl,
        onClick = onClick
    )
}

@Composable
fun Avatar(
    modifier: Modifier,
    avatarUrl: String,
    onClick: () -> Unit = {}
) {
    if (avatarUrl.isEmpty()) {
        EmptyAvatar(modifier)
    } else {
        Image(
            painter = rememberAsyncImagePainter(avatarUrl),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier
                .clip(CircleShape)
                .border(2.dp, AppTheme.colors.neutral5, CircleShape)
                .clickable(onClick = onClick)
        )
    }
}

@Composable
fun EmptyAvatar(
    modifier: Modifier
) {
    Image(
        painter = painterResource(id = com.nitishsharma.chatapp.R.drawable.ic_user_white),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(AppTheme.colors.neutral4),
        alignment = Alignment.BottomCenter,
        modifier = modifier
            .clip(CircleShape)
            .background(AppTheme.colors.neutral2)
            .border(2.dp, AppTheme.colors.neutral5, CircleShape)
            .padding(start = 3.dp, end = 3.dp, top = 10.dp)
    )
}

@Preview(showBackground = false)
@Composable
fun AvatarPreview() {
    HostAvatar(
        avatarSize = 72.dp,
        avatarUrl = ""
    )
}