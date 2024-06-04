package com.andka.hanashi.domain.usecase

import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.domain.contract.GetDetailUseCaseContract
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetDetailUseCase(private val storyRepository: StoryRepository) : GetDetailUseCaseContract {
    override operator fun invoke(id: String): Flow<ResultState<StoryEntity>> = flow {
        emit(ResultState.Loading())
        storyRepository.getStory(id).map {
            it.story.let { story ->
                StoryEntity(
                    id = story.id,
                    name = story.name,
                    photoUrl = story.photoUrl,
                    description = story.description,
                    createdAt = story.createdAt,
                    lat = story.lat,
                    long = story.lon
                )
            }
        }.catch {
            emit(ResultState.Error(message = it.message.toString()))
        }.collect { emit(ResultState.Success(it)) }
    }
}