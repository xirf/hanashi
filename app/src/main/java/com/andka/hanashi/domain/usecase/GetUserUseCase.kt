package com.andka.hanashi.domain.usecase

import com.andka.hanashi.domain.contract.GetUserUseCaseContract
import com.andka.hanashi.domain.entity.UserEntity
import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(private val userPreferenceRepository: UserPreferencesRepositoryInterface) :
    GetUserUseCaseContract {
    override operator fun invoke(): Flow<UserEntity> = userPreferenceRepository.getUserData
}