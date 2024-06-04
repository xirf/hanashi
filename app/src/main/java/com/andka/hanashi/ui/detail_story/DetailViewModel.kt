package com.andka.hanashi.ui.detail_story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andka.hanashi.domain.contract.GetDetailUseCaseContract
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.usecase.GetDetailUseCase
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DetailViewModel(private val getDetailUseCase: GetDetailUseCaseContract) : ViewModel() {
    data class DetailViewState(
        val resultGetDetail: ResultState<StoryEntity> = ResultState.Idle()
    )

    private val _detailViewState = MutableStateFlow(DetailViewState())
    val detailViewState get() = _detailViewState.asStateFlow()

    fun getDetail(id: String) {
        getDetailUseCase(id).onEach { res ->
            _detailViewState.value = _detailViewState.value.copy(resultGetDetail = res)
        }.launchIn(viewModelScope)
    }

    class Factory(private val getDetailUseCase: GetDetailUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(getDetailUseCase) as T
            }
            error("Unknown ViewModel class: ${modelClass.name}")
        }

    }
}