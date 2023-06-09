package com.nitishsharma.domain.api.models.canjoinroom

data class CanJoinRoom(
    val ownRoom: Boolean,
    val canJoin: Boolean,
    val actionForUser: String
)