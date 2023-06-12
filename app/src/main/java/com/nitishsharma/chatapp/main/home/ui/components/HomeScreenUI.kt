package com.nitishsharma.chatapp.main.home.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.main.ui.theme.AppTheme
import com.nitishsharma.chatapp.main.ui.utils.ActionableButton
import com.nitishsharma.chatapp.main.ui.utils.Avatar
import com.nitishsharma.chatapp.main.ui.utils.GroupAvatar
import com.nitishsharma.chatapp.utils.Utility
import com.nitishsharma.domain.api.models.roomsresponse.ActiveRooms

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenRoomItem(
    currentActiveRoom: ActiveRooms,
    firebaseAuth: FirebaseAuth,
    creatorAvatarUrl: String?,
    roomJoinerAvatarUrl: String?,
    onClickListener: (String) -> Unit,
    onLongPressListener: (ActiveRooms) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .height(height = 75.dp)
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = Color(0xff384a6b))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onLongPressListener.invoke(currentActiveRoom)
                    },
                    onTap = {
                        onClickListener.invoke(currentActiveRoom.roomId)
                    }
                )
            }
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (roomCreatorAvatarTop, creatorNameCv, roomNameCv, createdTimeCv, groupAvatar) = createRefs()
            Avatar(
                modifier = Modifier
                    .size(14.dp)
                    .constrainAs(roomCreatorAvatarTop) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start, margin = 25.dp)
                    },
                avatarUrl = creatorAvatarUrl!!
            )
            Text(
                text = currentActiveRoom.creatorName ?: "Null",
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.sans_regular)),
                    fontSize = 10.sp
                ),
                modifier = Modifier.constrainAs(creatorNameCv) {
                    start.linkTo(roomCreatorAvatarTop.end, margin = 5.dp)
                    top.linkTo(parent.top, margin = 10.dp)
                    bottom.linkTo(roomCreatorAvatarTop.bottom)
                }
            )
            Text(
                text = currentActiveRoom.roomName,
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.sans_med)),
                    fontSize = 18.sp
                ),
                modifier = Modifier.constrainAs(roomNameCv) {
                    start.linkTo(parent.start, margin = 25.dp)
                    top.linkTo(creatorNameCv.bottom, margin = 2.dp)
                }
            )
            Utility.formatDateTime(currentActiveRoom.createdAt)?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.sans_regular)),
                        fontSize = 8.sp
                    ),
                    color = AppTheme.colors.whitishWhite,
                    modifier = Modifier
                        .constrainAs(createdTimeCv) {
                            top.linkTo(roomNameCv.bottom, margin = 3.dp)
                            start.linkTo(parent.start, margin = 25.dp)
                        }
                        .wrapContentSize()
                        .background(
                            Color(0XFF253146),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(start = 5.dp, end = 5.dp, top = 0.5.dp, bottom = 0.5.dp)
                )
            }
            GroupAvatar(
                modifier = Modifier
                    .constrainAs(groupAvatar) {
                        end.linkTo(parent.end)
                        top.linkTo(creatorNameCv.top)
                        bottom.linkTo(createdTimeCv.bottom)
                    }
                    .padding(end = 15.dp),
                avatarSize = 28.dp,
                creatorAvatarUrl = creatorAvatarUrl,
                joinerAvatarUrl = roomJoinerAvatarUrl
            )
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
fun RandomButton(modifier: Modifier, onClick: () -> Unit) {
    ActionableButton(
        modifier = modifier,
        backgroundColor = AppTheme.colors.whitishWhite,
        onClick = {
            onClick.invoke()
        },
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
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.sans_bold))
            )
        )
        Text(
            text = "Your Rooms Will Appear Here",
            color = Color.White,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily(Font(R.font.inter_light))
            )
        )
    }
}

@Composable
fun FloatingActionMenu(
    modifier: Modifier = Modifier,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit
) {
    val isMenuExpanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(bottom = 85.dp, end = 15.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        if (isMenuExpanded.value) {
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuExpanded.value = false
                    onCreateRoom.invoke()
                },
                text = { Text("Create Room", color = Color.White) },
                backgroundColor = AppTheme.colors.chatRoomLightBlue,
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExtendedFloatingActionButton(
                onClick = {
                    isMenuExpanded.value = false
                    onJoinRoom.invoke()
                },
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
                            fontSize = 18.sp,
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
                            fontSize = 18.sp,
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
fun ShimmerItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .height(75.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xff384a6b))
    ) {
        ShimmerAnimation(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun ShimmerAnimation(modifier: Modifier) {
    val colors = listOf(
        Color.LightGray.copy(alpha = 0.8f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.5f),
        Color.LightGray.copy(alpha = 0.8f),
    )
    val gradientWidth = 100f

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = colors,
                        start = Offset.Zero,
                        end = Offset(gradientWidth, 0f),
                    )
                )
        )
    }
}

@Composable
@Preview
fun ModalDrawerSample() {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val scope = rememberCoroutineScope()
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalDrawer(
            modifier = Modifier
                .fillMaxHeight()
                .width(220.dp),
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    DrawerContent()
                }
            },
            content = {
                "HomeScreenUI()"
            }
        )
    }
}

@Composable
fun DrawerContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(1.dp)
            .verticalScroll(rememberScrollState())
            .clipToBounds(),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Quick Access",
                style = TextStyle(fontSize = 12.sp),
                fontFamily = FontFamily(Font(R.font.sharp_grotesk_medium20)),
                color = Color(0xFF7E7E7E)
            )
            Column(modifier = Modifier) {
                DrawerItem(
                    icon = painterResource(id = R.drawable.ic_logout),
                    text = "Sign out",
                    onClick = {
                        // Handle sign out action
                    }
                )
            }
        }
    }
}

@Composable
fun DrawerItem(
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onClick.invoke() }
            .padding(vertical = 19.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = TextStyle(fontSize = 14.sp),
            fontFamily = FontFamily(Font(R.font.sharp_grotesk_medium20))
        )
    }
}

fun customShape() = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f, 0f, 100f /* width */, 131f /* height */))
    }
}

