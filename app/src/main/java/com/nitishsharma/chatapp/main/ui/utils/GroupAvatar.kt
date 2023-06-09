package com.nitishsharma.chatapp.main.ui.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GroupAvatar(
    modifier: Modifier,
    avatarSize: Dp,
    creatorAvatarUrl: String,
    joinerAvatarUrl: String? = null
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        joinerAvatarUrl?.let {
            Avatar(
                avatarUrl = it,
                modifier = Modifier.size(avatarSize)
            )
        }
        Avatar(
            avatarUrl = creatorAvatarUrl,
            modifier = Modifier
                .size(avatarSize)
                .offset((-10).dp)
        )

    }
}

@Composable
fun GroupAvatar2(
    modifier: Modifier,
    avatarSize: Dp,
    creatorAvatarUrl: String,
    joinerAvatarUrl: String? = null
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            avatarUrl = creatorAvatarUrl,
            modifier = Modifier
                .size(avatarSize)
        )
        joinerAvatarUrl?.let {
            Avatar(
                avatarUrl = it,
                modifier = Modifier
                    .size(avatarSize)
                    .offset((-10).dp)
            )
        }
    }
}

@Preview
@Composable
fun ShowGroup() {
    GroupAvatar(modifier = Modifier, avatarSize = 25.dp, "", "")
}
