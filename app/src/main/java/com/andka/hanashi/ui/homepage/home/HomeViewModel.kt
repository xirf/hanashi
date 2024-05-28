package com.andka.hanashi.ui.homepage.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.usecase.GetStoriesUseCase
import com.andka.hanashi.domain.usecase.GetUserUseCase
import com.andka.hanashi.ui.homepage.MainActivityViewModel
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val getStoriesUseCase: GetStoriesUseCase
) : ViewModel() {

    data class ProfileFragmentViewState(
        val resultGetUser: ResultState<Boolean> = ResultState.Idle(),
        val resultGetStory: ResultState<List<StoryEntity>> = ResultState.Idle(),
        val username: String = ""
    )

    private val _storyState = MutableStateFlow(ProfileFragmentViewState())
    val storyState = _storyState

    fun getStories() {
        viewModelScope.launch {
            getStoriesUseCase().collect { stories ->
                _storyState.update { it.copy(resultGetStory = stories) }
            }
        }
    }

    class Factory(
        private val getUserUseCase: GetUserUseCase,
        private val getStoriesUseCase: GetStoriesUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(getUserUseCase, getStoriesUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }

}