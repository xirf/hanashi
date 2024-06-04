package com.andka.hanashi.domain.usecase

import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.domain.contract.GetStoriesWithLocationUseCaseContract
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetStoriesWithLocationUseCase(private val storyRepository: StoryRepository) :
    GetStoriesWithLocationUseCaseContract {
    override operator fun invoke(): Flow<ResultState<List<StoryEntity>>> = flow {
        emit(ResultState.Loading())
        storyRepository.getStoriesWithLocation().map {
            it.listStory.map { story ->
                StoryEntity(
                    id = story.id,
                    name = story.name,
                    description = story.description,
                    photoUrl = story.photoUrl,
                    lat = story.lat,
                    long = story.lon,
                    createdAt = story.createdAt
                )
            }
        }.catch { e ->
            emit(ResultState.Error(message = e.message ?: "Error occurred while fetching stories"))
        }.collect { stories ->
            emit(ResultState.Success(stories))
        }

    }
}