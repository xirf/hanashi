package com.andka.hanashi.fake

import com.andka.hanashi.domain.contract.LogoutUseCaseContract

class FakeLogoutUseCase() : LogoutUseCaseContract {
    override suspend operator fun invoke() = Unit
}