package com.andka.hanashi.domain.interfaces

import com.andka.hanashi.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepositoryInterface {
    val getUserData: Flow<UserEntity>
    suspend fun saveUserData(userEntity: UserEntity)
    suspend fun clearUserData()
}