package com.andka.hanashi.domain.usecase

import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map


class GetStoriesUseCase(private val storyRepository: StoryRepository) {
    operator fun invoke(): Flow<ResultState<List<StoryEntity>>> = flow {
        emit(ResultState.Loading())
        storyRepository.getStories().map {
            it.listStory.map { story ->
                StoryEntity(
                    id = story.id,
                    name = story.name,
                    description = story.description,
                    photoUrl = story.photoUrl,
                )
            }
        }.catch {
            emit(ResultState.Error(message = it.message.toString()))
        }.collect {
            emit(ResultState.Success(it))
        }
    }

}