package com.andka.hanashi.domain.usecase

import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface

class LogoutUseCase(private val userPreferenceRepository: UserPreferencesRepositoryInterface) {
    suspend operator fun invoke() = userPreferenceRepository.clearUserData()
}