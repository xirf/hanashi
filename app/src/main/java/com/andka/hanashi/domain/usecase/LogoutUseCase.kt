package com.andka.hanashi.domain.usecase

import com.andka.hanashi.domain.contract.LogoutUseCaseContract
import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface

class LogoutUseCase(private val userPreferenceRepository: UserPreferencesRepositoryInterface) :
    LogoutUseCaseContract {
    override suspend operator fun invoke() = userPreferenceRepository.clearUserData()
}