package com.andka.hanashi.ui.homepage.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.usecase.GetUserUseCase
import com.andka.hanashi.domain.usecase.LogoutUseCase
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    data class ProfileFragmentViewState(
        val resultGetUser: ResultState<String> = ResultState.Idle(),
        val username: String = ""
    )

    private val _userState = MutableStateFlow(ProfileFragmentViewState())
    val userState = _userState

    fun getUser() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _userState.update { it.copy(username = user.name) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }


    class Factory(
        private val getUserUseCase: GetUserUseCase,
        private val logoutUseCase: LogoutUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                return ProfileViewModel(getUserUseCase, logoutUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}