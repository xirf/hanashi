package com.andka.hanashi.domain.interfaces

import com.andka.hanashi.data.response.GeneralResponse
import com.andka.hanashi.data.response.LoginResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepositoryInterface {
    fun register(name: String, email: String, password: String): Flow<GeneralResponse>
    fun login(email: String, password: String): Flow<LoginResponse>
}