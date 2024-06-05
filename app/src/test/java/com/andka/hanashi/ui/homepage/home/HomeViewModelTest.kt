package com.andka.hanashi.ui.homepage.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import androidx.recyclerview.widget.ListUpdateCallback
import com.andka.hanashi.data.response.StoryResponse
import com.andka.hanashi.domain.entity.StoryEntity
import com.andka.hanashi.dummy.DummyData
import com.andka.hanashi.fake.FakeGetStoriesUseCase
import com.andka.hanashi.fake.FakeGetUserUseCase
import com.andka.hanashi.fake.FakeLogoutUseCase
import com.andka.hanashi.utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner


/*
    =================== FUTURE NOTE ====================
    This test need some work in mocking android.util.Log
    Some workaround in stackoverflow not working include
    using custom log on test.java.android.util

    To fix this i use test option statement
    unitTests.isReturnDefaultValues = true

    From Docs:
    Caution: Setting the returnDefaultValues property
    to true should be done with care. The null/zero
    return values can introduce regressions in your
    tests, which are hard to debug and might allow
    failing tests to pass. Only use it as a last resort.
 */

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    private val getStoriesUseCase = FakeGetStoriesUseCase()
    private val getUserUseCase = FakeGetUserUseCase()
    private val getLogoutUseCase = FakeLogoutUseCase()


    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DummyData.generateDummyStoryResponse()
        val data: PagingData<StoryResponse> = StoryPagingSource.snapshot(dummyStory)
        val homeViewModel = HomeViewModel(getStoriesUseCase, getLogoutUseCase, getUserUseCase)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.itemDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        homeViewModel.getStories()
        getStoriesUseCase.fakeDelegate.emit(data.map { toStoryEntity(it) })
        differ.submitData(homeViewModel.storyState.value.resultGetStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory.map { toStoryEntity(it) }.first(), differ.snapshot().first())
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryEntity> = PagingData.empty()
        val storyViewModel = HomeViewModel(getStoriesUseCase, getLogoutUseCase, getUserUseCase)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.itemDiffCallback,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        storyViewModel.getStories()
        getStoriesUseCase.fakeDelegate.emit(data)
        differ.submitData(storyViewModel.storyState.value.resultGetStory)
        Assert.assertEquals(0, differ.snapshot().size)
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

fun toStoryEntity(storyResponse: StoryResponse): StoryEntity {
    return StoryEntity(
        id = storyResponse.id,
        name = storyResponse.name,
        description = storyResponse.description,
        photoUrl = storyResponse.photoUrl,
        lat = storyResponse.lat,
        long = storyResponse.lon,
        createdAt = storyResponse.createdAt
    )
}