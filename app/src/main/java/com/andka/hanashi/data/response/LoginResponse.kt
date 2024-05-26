package com.andka.hanashi.data.response

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: LoginResult
)

data class LoginResult(
    val name: String,
    val token: String,
    val userId: String
)
