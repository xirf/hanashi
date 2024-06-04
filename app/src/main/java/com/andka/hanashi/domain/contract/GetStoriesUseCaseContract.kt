package com.andka.hanashi.domain.contract

import androidx.paging.PagingData
import com.andka.hanashi.domain.entity.StoryEntity
import kotlinx.coroutines.flow.Flow


interface GetStoriesUseCaseContract {
    operator fun invoke(): Flow<PagingData<StoryEntity>>
}
