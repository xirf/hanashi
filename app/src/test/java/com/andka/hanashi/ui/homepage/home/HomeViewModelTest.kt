package com.andka.hanashi.ui.homepage.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.andka.hanashi.data.response.StoryResponse
import com.andka.hanashi.dummy.DummyData
import com.andka.hanashi.fake.GetStoriesUseCase
import com.andka.hanashi.fake.GetUserUseCase
import com.andka.hanashi.fake.LogoutUseCase
import com.andka.hanashi.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
internal class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private val getStoriesUseCase = GetStoriesUseCase()
    private val getUserUseCase = GetUserUseCase()
    private val getLogoutUseCase = LogoutUseCase()


    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DummyData.generateDummyStoryResponse()
        val data: PagingData<StoryResponse> = StoryPagingSource.snapshot(dummyStory)
        val homeViewModel=HomeViewModel(getStoriesUseCase, getUserUseCase, getLogoutUseCase)
    }

    class StoryPagingSource : PagingSource<Int, LiveData<List<StoryResponse>>>() {
        companion object {
            fun snapshot(data: List<StoryResponse>): PagingData<StoryResponse> {
                return PagingData.from(data)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryResponse>>>): Int = 0
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryResponse>>> =
            LoadResult.Page(emptyList(), 0, 1)

    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}