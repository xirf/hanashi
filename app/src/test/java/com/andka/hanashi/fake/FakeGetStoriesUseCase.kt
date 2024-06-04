package com.andka.hanashi.fake

import androidx.paging.PagingData
import com.andka.hanashi.domain.contract.GetStoriesUseCaseContract
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.utils.FakeFlowDelegate
import kotlinx.coroutines.flow.Flow

class FakeGetStoriesUseCase : GetStoriesUseCaseContract {
    val fakeDelegate = FakeFlowDelegate<PagingData<StoryEntity>>()
    override operator fun invoke(): Flow<PagingData<StoryEntity>> =
        fakeDelegate.flow
}