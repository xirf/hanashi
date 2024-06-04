package com.andka.hanashi.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.andka.hanashi.domain.contract.GetStoriesUseCaseContract
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.interfaces.StoryRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class GetStoriesUseCase(private val storyRepository: StoryRepositoryInterface) :
    GetStoriesUseCaseContract {
    override operator fun invoke(): Flow<PagingData<StoryEntity>> =
        storyRepository.getStories().map { pagingData ->
            pagingData.map { storyResponse ->
                StoryEntity(
                    id = storyResponse.id,
                    name = storyResponse.name,
                    photoUrl = storyResponse.photoUrl,
                    description = storyResponse.description,
                    createdAt = storyResponse.createdAt,
                    lat = storyResponse.lat,
                    long = storyResponse.lon
                )
            }
        }

}
