package com.nitishsharma.chatapp.main.chats

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.main.ui.utils.GroupAvatar2

@Composable
fun ChatTopView(
    creatorAvtarUrl: String,
    joinerAvatarUrl: String?,
    creatorName: String,
    joinerName: String?,
    roomName: String?
) {
    ConstraintLayout {
        val (groupAvatar, roomNameCv, namesCv) = createRefs()
        GroupAvatar2(
            modifier = Modifier.constrainAs(groupAvatar) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            },
            avatarSize = 30.dp,
            creatorAvatarUrl = creatorAvtarUrl,
            joinerAvatarUrl = joinerAvatarUrl
        )
        Text(
            text = roomName ?: "Your Room",
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.sans_bold)),
                fontSize = 18.sp
            ),
            modifier = Modifier.constrainAs(roomNameCv) {
                start.linkTo(groupAvatar.end, 5.dp)
                top.linkTo(groupAvatar.top)
                bottom.linkTo(namesCv.top)
            }
        )
        Text(
            text = "$creatorName ${joinerName ?: ""}",
            color = Color.White,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.sans_med)),
                fontSize = 14.sp
            ),
            modifier = Modifier.constrainAs(namesCv) {
                start.linkTo(groupAvatar.end, 5.dp)
                bottom.linkTo(groupAvatar.bottom)
                top.linkTo(roomNameCv.bottom)
            }
        )
    }
}

@Preview
@Composable
fun ShowChatTop() {
    ChatTopView(
        creatorAvtarUrl = "",
        joinerAvatarUrl = "",
        creatorName = "Nitish",
        joinerName = null,
        roomName = "2 Yaar, 4 Peg"
    )
}