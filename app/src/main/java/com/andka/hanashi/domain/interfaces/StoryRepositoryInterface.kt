package com.andka.hanashi.domain.interfaces

import com.andka.hanashi.data.response.GeneralResponse
import com.andka.hanashi.data.response.StoryDetailResponse
import com.andka.hanashi.data.response.StoryListResponse
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StoryRepositoryInterface {
    fun getStories(): Flow<StoryListResponse>
    fun getStory(id: String): Flow<StoryDetailResponse>
    fun addStory(file: File, description: String): Flow<GeneralResponse>
}