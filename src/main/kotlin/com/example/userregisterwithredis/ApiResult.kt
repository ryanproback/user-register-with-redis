package com.example.userregisterwithredis

data class ApiResult<T>(
    val code: String,
    val body: T?
) {
    companion object {
        fun <T> success(body: T? = null) = ApiResult("", body)
        fun <T> error(code: String, body: T? = null) = ApiResult(code, body)
    }
}
