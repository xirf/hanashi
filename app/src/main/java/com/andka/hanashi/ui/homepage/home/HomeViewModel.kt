package com.andka.hanashi.ui.homepage.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.usecase.GetStoriesUseCase
import com.andka.hanashi.domain.usecase.GetUserUseCase
import com.andka.hanashi.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getStoriesUseCase: GetStoriesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    data class HomeFragmentViewState(
        val resultGetStory: PagingData<StoryEntity> = PagingData.empty(),
        val username: String = ""
    )

    private val _storyState = MutableStateFlow(HomeFragmentViewState())
    val storyState = _storyState

    fun getStories() {
        viewModelScope.launch {
            getStoriesUseCase().cachedIn(viewModelScope).collect { stories ->
                _storyState.update { it.copy(resultGetStory = stories) }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _storyState.update { it.copy(username = user.name) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    class Factory(
        private val getStoriesUseCase: GetStoriesUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val getUserUseCase: GetUserUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(getStoriesUseCase, logoutUseCase, getUserUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }

}