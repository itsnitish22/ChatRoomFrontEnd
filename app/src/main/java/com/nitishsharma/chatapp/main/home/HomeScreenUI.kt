package com.nitishsharma.chatapp.main.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.main.ui.theme.AppTheme
import com.nitishsharma.chatapp.main.ui.utils.ActionableButton
import com.nitishsharma.chatapp.main.ui.utils.Avatar

@Composable
fun HomeScreenUI() {
    Surface(color = AppTheme.colors.appBackgroundLightGray) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (appName, avatar, darkSurface, randomButton, centerNoRoomsDisplay, floatingActionButton) = createRefs()
            AppName(modifier = Modifier.constrainAs(appName) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            })
            Avatar(
                modifier = Modifier
                    .size(45.dp)
                    .padding(end = 10.dp, top = 10.dp)
                    .constrainAs(avatar) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    }, avatarUrl = ""
            )
            Surface(modifier = Modifier
                .constrainAs(darkSurface) {
                    top.linkTo(appName.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 5.dp)
                .fillMaxSize(),
                color = AppTheme.colors.appBackgroundDarkGray,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                MiddleNoActiveRooms(modifier = Modifier.constrainAs(centerNoRoomsDisplay) {
                    centerTo(darkSurface)
                })
                FloatingActionMenu(
                    modifier = Modifier
                        .constrainAs(floatingActionButton) {
                            end.linkTo(darkSurface.end, margin = 16.dp)
                            bottom.linkTo(darkSurface.bottom, margin = 16.dp)
                        }
                )
            }
            RandomButton(modifier = Modifier
                .constrainAs(randomButton) {
                    end.linkTo(darkSurface.end)
                    top.linkTo(darkSurface.top)
                }
                .padding(top = 15.dp, end = 15.dp))
        }
    }
}

@Composable
fun AppName(modifier: Modifier) {
    Row(
        modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Chat",
            color = AppTheme.colors.chatRoomLightBlue,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.sans_bold)),
                fontSize = 25.sp
            )
        )
        Text(
            text = "Room",
            color = AppTheme.colors.chatRoomLightGreen,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.sans_bold)),
                fontSize = 25.sp
            )
        )
    }
}

@Composable
fun RandomButton(modifier: Modifier) {
    ActionableButton(
        modifier = modifier,
        backgroundColor = AppTheme.colors.whitishWhite,
        onClick = { /*TODO*/ },
        textOnButton = "Random"
    )
}

@Composable
fun MiddleNoActiveRooms(modifier: Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 50.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_emptyroom),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color(0xff64a6aa)),
            modifier = modifier
                .width(width = 52.dp)
                .height(height = 52.dp)
        )
        Text(
            text = "No Active Rooms Available",
            color = Color.White,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sans_bold))
            )
        )
        Text(
            text = "Your Rooms Will Appear Here",
            color = Color.White,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily(Font(R.font.inter_light))
            )
        )
    }
}

@Preview
@Composable
fun FloatingActionMenu(modifier: Modifier = Modifier) {
    val isMenuExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(bottom = 85.dp, end = 15.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        if (isMenuExpanded.value) {
            ExtendedFloatingActionButton(
                onClick = { /* Handle option 1 */ },
                text = { Text("Create Room", color = Color.White) },
                backgroundColor = AppTheme.colors.chatRoomLightBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExtendedFloatingActionButton(
                onClick = { /* Handle option 2 */ },
                text = { Text("Join Room", color = Color.White) },
                backgroundColor = AppTheme.colors.chatRoomLightBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                text = {
                    Text(
                        "Dismiss", color = Color.White, style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sans_bold))
                        )
                    )
                },
                onClick = {
                    isMenuExpanded.value = false
                },
                backgroundColor = AppTheme.colors.chatRoomLightBlue
            )
        } else {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                text = {
                    Text(
                        "Room", color = Color.White, style = TextStyle(
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.sans_bold))
                        )
                    )
                },
                onClick = {
                    isMenuExpanded.value = true
                },
                backgroundColor = AppTheme.colors.chatRoomLightBlue
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PreviewHomeUI() {
    HomeScreenUI()
}