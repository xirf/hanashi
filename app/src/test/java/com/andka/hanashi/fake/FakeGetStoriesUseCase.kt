package com.andka.hanashi.fake

import androidx.paging.PagingData
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.domain.usecase.GetStoriesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGetStoriesUseCase: GetStoriesUseCase() {
    val fakeDelegate = FakeFlowDelegate<PagingData<StoryEntity>>()
    override operator fun invoke(): Flow<PagingData<StoryEntity>> = fakeDelegate.flow
}

class FakeFlowDelegate<T>{
    val flow: MutableSharedFlow<T> = MutableSharedFlow()
    suspend fun emit(v: T) = flow.emit(v)
}