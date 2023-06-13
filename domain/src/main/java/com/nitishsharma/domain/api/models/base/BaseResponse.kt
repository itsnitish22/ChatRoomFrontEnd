package com.nitishsharma.domain.api.models.base

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BaseResponse<T> : Serializable {
    @SerializedName("status_code")
    val statusCode: Int = 0

    @SerializedName("message")
    val message: String? = null

    @SerializedName("data")
    val data: T? = null
}