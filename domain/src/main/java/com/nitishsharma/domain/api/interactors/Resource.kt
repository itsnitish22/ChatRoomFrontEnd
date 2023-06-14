package com.nitishsharma.domain.api.interactors

sealed class Resource<T>(val data: T? = null, val error: Exception? = null) {
    class Success<T>(data: T) : Resource<T>(data) {
        fun data(): T {
            return data!!
        }
    }

    class Error<T>(error: Exception? = null) : Resource<T>(error = error)
    open class Loading<T> : Resource<T>()
}