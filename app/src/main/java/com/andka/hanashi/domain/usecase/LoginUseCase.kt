package com.andka.hanashi.domain.usecase

import com.andka.hanashi.data.repository.AuthRepository
import com.andka.hanashi.domain.entity.UserEntity
import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val userPreferencesRepository: UserPreferencesRepositoryInterface,
    private val authRepository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading())
        authRepository.login(email, password).catch {
            emit(ResultState.Error(it.message.toString()))
        }.collect { res ->
            if (res.error) {
                emit(ResultState.Error(res.message))
            } else {
                res.loginResult.let {
                    userPreferencesRepository.saveUserData(
                        UserEntity(
                            id = it.userId,
                            name = it.name,
                            token = it.token
                        )
                    )
                }
                emit(ResultState.Success(res.message))
            }
        }
    }
}