package com.andka.hanashi.domain.contract

import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface GetStoriesWithLocationUseCaseContract {
    operator fun invoke(): Flow<ResultState<List<StoryEntity>>>
}