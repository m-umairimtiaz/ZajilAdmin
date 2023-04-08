package com.example.zajiladmin.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val fullUpdate: Boolean = false
) {
    class Success<T>(data: T,fullUpdate: Boolean = false) : Resource<T>(data,fullUpdate =fullUpdate)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}