package com.andka.hanashi.data.source.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.andka.hanashi.data.response.StoryResponse
import com.andka.hanashi.data.source.remote.ApiService

class StoryPagingSource(private val apiService: ApiService) : PagingSource<Int, StoryResponse>() {
    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { pos ->
            val anchorPage = state.closestPageToPosition(pos)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return try {
            val nextPageNumber = params.key ?: 1
            val response = apiService.getStories(nextPageNumber, params.loadSize)
            LoadResult.Page(
                data = response.listStory,
                prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                nextKey = if (response.listStory.isEmpty()) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}