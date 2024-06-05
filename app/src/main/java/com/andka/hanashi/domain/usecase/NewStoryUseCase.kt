package com.andka.hanashi.domain.usecase

import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.domain.contract.NewStoryUseCaseContract
import com.andka.hanashi.utils.ResultState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.File

class NewStoryUseCase(private val storyRepository: StoryRepository) : NewStoryUseCaseContract {
    override operator fun invoke(file: File, description: String, latLng: LatLng?): Flow<ResultState<String>> =
        flow {
            emit(ResultState.Loading())
            storyRepository.addStory(file, description, latLng).catch {
                emit(ResultState.Error(message = it.message.toString()))
            }.collect {
                emit(ResultState.Success(it.message))
            }
        }
}