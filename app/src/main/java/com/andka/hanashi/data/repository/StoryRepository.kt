package com.andka.hanashi.data.repository

import com.andka.hanashi.data.source.remote.ApiService
import com.andka.hanashi.domain.interfaces.StoryRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(private val apiService: ApiService) : StoryRepositoryInterface {
    override fun getStories() = flow {
        emit(apiService.getStories())
    }.flowOn(Dispatchers.IO)

    override fun getStory(id: String) = flow {
        emit(apiService.getStoryDetail(id))
    }.flowOn(Dispatchers.IO)

    override fun addStory(file: File, description: String) = flow {
        val body = MultipartBody.Part.createFormData(
            "photo", file.name, file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        emit(apiService.addStory(body, description.toRequestBody("text/plain".toMediaType())))
    }.flowOn(Dispatchers.IO)
}