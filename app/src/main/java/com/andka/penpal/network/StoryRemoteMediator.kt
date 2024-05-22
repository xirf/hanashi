package com.andka.penpal.network

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.andka.penpal.database.entity.StoryRemoteKeyEntity
import com.andka.penpal.database.StoryDatabase
import com.andka.penpal.domain.ListStoryItem
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: APIService,
    private val token: String
) : RemoteMediator<Int, ListStoryItem>() {
    companion object {
        const val STARTING_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, ListStoryItem>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> STARTING_PAGE_INDEX
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKeys =
                    database.storyRemoteKeyDao().getRemoteKeysId(state.lastItemOrNull()?.id ?: "")
                if (remoteKeys?.nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKeys.nextKey
            }
        }

        return try {
            val apiResponse = apiService.getStories(token, page, 10).listStory
            val isEndOfPagination = apiResponse.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.storyRemoteKeyDao().deleteRemoteKeys()
                    database.storyDao().delete()
                }

                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfPagination) null else page + 1
                val key = apiResponse.map {
                    StoryRemoteKeyEntity(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.storyRemoteKeyDao().insertAll(key)
                database.storyDao().insert(apiResponse)
            }

            MediatorResult.Success(endOfPaginationReached = isEndOfPagination)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }
}