package com.nitishsharma.domain.api.models.otheroomsarray

data class GetDistinctRoomIdsFromArray(
    val count: Int,
    val active_rooms: ArrayList<String>
)