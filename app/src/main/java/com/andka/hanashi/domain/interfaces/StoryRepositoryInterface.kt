package com.andka.hanashi.domain.interfaces

import androidx.paging.PagingData
import com.andka.hanashi.data.response.GeneralResponse
import com.andka.hanashi.data.response.StoryDetailResponse
import com.andka.hanashi.data.response.StoryListResponse
import com.andka.hanashi.data.response.StoryResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepositoryInterface {
    fun getStories(): Flow<PagingData<StoryResponse>>
    fun getStory(id: String): Flow<StoryDetailResponse>
    fun addStory(file: File, description: String): Flow<GeneralResponse>
    fun getStoriesWithLocation(): Flow<StoryListResponse>
}