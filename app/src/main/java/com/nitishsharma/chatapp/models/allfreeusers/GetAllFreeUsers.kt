package com.nitishsharma.chatapp.models.allfreeusers

import com.google.gson.annotations.SerializedName

data class GetAllFreeUsers(
    @SerializedName("count")
    val count: Int,
    @SerializedName("free_users")
    val freeUsers: ArrayList<FreeUsers>
)

data class FreeUsers(
    @SerializedName("id")
    val id: Int,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_avatar")
    val userAvatar: String,
    @SerializedName("is_free")
    val isFree: Boolean
)
