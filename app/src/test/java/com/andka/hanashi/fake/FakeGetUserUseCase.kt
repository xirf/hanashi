package com.andka.hanashi.fake

import com.andka.hanashi.domain.entity.UserEntity
import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetUserUseCase() {
    operator fun invoke(): Flow<UserEntity> = FakeFlowDelegate<UserEntity>().flow
}