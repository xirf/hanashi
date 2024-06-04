package com.andka.hanashi.ui.new_story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.contract.NewStoryUseCaseContract
import com.andka.hanashi.domain.usecase.NewStoryUseCase
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.io.File

class NewStoryViewModel(
    private val newStoryUseCase: NewStoryUseCaseContract
) : ViewModel() {
    data class NewStoryViewState(
        val resultNewStory: ResultState<String> = ResultState.Idle()
    )

    private val _newStoryState = MutableStateFlow(NewStoryViewState())
    val newStoryState = _newStoryState.asStateFlow()

    fun newStory(file: File, description: String) {
        newStoryUseCase(file, description).onEach { res ->
            _newStoryState.update { it.copy(resultNewStory = res) }
        }.launchIn(viewModelScope)
    }

    class Factory(
        private val newStoryUseCase: NewStoryUseCaseContract
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewStoryViewModel::class.java)) {
                return NewStoryViewModel(newStoryUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}