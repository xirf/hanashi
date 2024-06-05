package com.andka.hanashi.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andka.hanashi.data.response.StoryResponse
import com.andka.hanashi.data.source.database.StoryDatabase
import com.andka.hanashi.data.source.remote.ApiService
import com.andka.hanashi.data.source.remote.paging.StoryRemoteMediator
import com.andka.hanashi.domain.interfaces.StoryRepositoryInterface
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@OptIn(ExperimentalPagingApi::class)
class StoryRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
) : StoryRepositoryInterface {
    private val storyRemoteMediator = StoryRemoteMediator(apiService, storyDatabase)
    override fun getStories(): Flow<PagingData<StoryResponse>> {
        return Pager(
            config = PagingConfig(pageSize = 15),
            remoteMediator = storyRemoteMediator,
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStories()
            }
        ).flow
    }

    override fun getStory(id: String) = flow {
        emit(apiService.getStoryDetail(id))
    }.flowOn(Dispatchers.IO)

    override fun addStory(file: File, description: String, latLng: LatLng?) = flow {
        val body = MultipartBody.Part.createFormData(
            "photo", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        emit(apiService.addStory(
            body,
            description.toRequestBody("text/plain".toMediaType()),
            latLng?.latitude?.toFloat(),
            latLng?.longitude?.toFloat(),
        ))
    }.flowOn(Dispatchers.IO)

    override fun getStoriesWithLocation() = flow {
        emit(apiService.storiesWithLocation())
    }.flowOn(Dispatchers.IO)

}