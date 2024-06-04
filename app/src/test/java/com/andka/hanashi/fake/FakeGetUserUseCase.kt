package com.andka.hanashi.fake

import com.andka.hanashi.domain.contract.GetUserUseCaseContract
import com.andka.hanashi.domain.entity.UserEntity
import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface
import com.andka.hanashi.utils.FakeFlowDelegate
import kotlinx.coroutines.flow.Flow

class FakeGetUserUseCase():GetUserUseCaseContract {
    override operator fun invoke(): Flow<UserEntity> = FakeFlowDelegate<UserEntity>().flow
}