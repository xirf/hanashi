package com.andka.hanashi.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.contract.LoginUseCaseContract
import com.andka.hanashi.domain.usecase.LoginUseCase
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class LoginViewModel(private val loginUseCase: LoginUseCaseContract) : ViewModel() {
    data class LoginViewState(
        val resultVerifyUser: ResultState<String> = ResultState.Idle()
    )

    private val _isLoggedIn = MutableStateFlow(LoginViewState())
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun login(email: String, password: String) {
        loginUseCase(email, password).onEach { result ->
            _isLoggedIn.update { it.copy(resultVerifyUser = result) }
        }.launchIn(viewModelScope)
    }

    class Factory(
        private val loginUseCase: LoginUseCaseContract
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(loginUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}