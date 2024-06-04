package com.andka.hanashi.domain.contract

import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface LoginUseCaseContract {
    operator fun invoke(email: String, password: String): Flow<ResultState<String>>
}