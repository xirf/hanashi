package com.andka.hanashi.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.usecase.GetStoriesUseCase
import com.andka.hanashi.domain.usecase.GetUserUseCase
import com.andka.hanashi.domain.usecase.LogoutUseCase
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getStoriesUseCase: GetStoriesUseCase
) : ViewModel() {
    data class MainActivityViewState(
        val resultGetUser: ResultState<Boolean> = ResultState.Idle(),
        val resultGetStory: ResultState<List<StoryEntity>> = ResultState.Idle(),
        val username: String = ""
    )

    private val _isLoggedIn = MutableStateFlow(MainActivityViewState())
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _storyState = MutableStateFlow(MainActivityViewState())
    val storyState = _storyState.asStateFlow()


    init {
        getIsLoggedIn()
    }

    fun getStories() {
        viewModelScope.launch {
            getStoriesUseCase().collect { stories ->
                _storyState.update { it.copy(resultGetStory = stories) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _storyState.update { it.copy(username = user.name) }
            }
        }
    }

    private fun getIsLoggedIn() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                delay(2000)
                _isLoggedIn.update { it.copy(resultGetUser = ResultState.Success(user.token.isNotEmpty())) }
            }
        }
    }

    class Factory(
        private val getUserUseCase: GetUserUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val getStoriesUseCase: GetStoriesUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                return MainActivityViewModel(getUserUseCase, logoutUseCase, getStoriesUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}