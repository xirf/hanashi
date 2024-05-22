package com.andka.penpal.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.andka.penpal.database.StoryDatabase
import com.andka.penpal.network.APIService
import com.andka.penpal.network.StoryRemoteMediator
import com.andka.penpal.domain.ListStoryItem

@OptIn(ExperimentalPagingApi::class)
class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: APIService,
    private val token: String
) {
    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = { storyDatabase.storyDao().getAll() }
        ).liveData
    }
}