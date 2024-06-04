package com.andka.hanashi.domain.contract

import com.andka.hanashi.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface GetUserUseCaseContract {
    operator fun invoke(): Flow<UserEntity>
}