package com.andka.hanashi.ui.homepage.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.contract.GetStoriesWithLocationUseCaseContract
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.usecase.GetStoriesWithLocationUseCase
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapsViewModel(private val getStoriesWithLocationUseCase: GetStoriesWithLocationUseCaseContract) :
    ViewModel() {
    data class MapsViewState(
        val mapStories: ResultState<List<StoryEntity>> = ResultState.Idle()
    )

    private val _mapsViewState = MutableStateFlow(MapsViewState())
    val mapsViewState = _mapsViewState.asStateFlow()

    fun getStoriesWithLocation() {
        viewModelScope.launch {
            getStoriesWithLocationUseCase().collect { stories ->
                _mapsViewState.update { it.copy(mapStories = stories) }
            }
        }
    }

    class Factory(private val getStoriesWithLocationUseCase: GetStoriesWithLocationUseCaseContract) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                return MapsViewModel(getStoriesWithLocationUseCase) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }

}