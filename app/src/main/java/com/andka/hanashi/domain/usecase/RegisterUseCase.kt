package com.andka.hanashi.domain.usecase

import com.andka.hanashi.data.repository.AuthRepository
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(name: String, email: String, password: String): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading())
            authRepository.register(name, email, password).catch { err ->
                emit(ResultState.Error(err.message.toString()))
            }.collect { res ->
                if (!res.error) {
                    emit(ResultState.Success(res.message))
                } else {
                    emit(ResultState.Error(res.message))
                }
            }
        }
}