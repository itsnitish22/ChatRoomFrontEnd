package com.nitishsharma.chatapp.main.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.utils.Utility.bounceClick

@Composable
fun ActionableButton(
    modifier: Modifier,
    backgroundColor: Color,
    onClick: () -> Unit,
    textOnButton: String
) {
    Box(
        modifier = modifier
            .bounceClick()
            .clip(RoundedCornerShape(25.dp))
            .background(backgroundColor)
            .height(35.dp)
            .width(108.dp)
            .clickable(enabled = true, onClick = { onClick }),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_random),
                contentDescription = null,
                tint = Color(0xff9747ff),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = textOnButton,
                color = Color.Black,
                style = TextStyle(
                    fontFamily = FontFamily(Font(com.nitishsharma.chatapp.R.font.sans_med)),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}