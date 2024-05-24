package com.andka.penpal.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andka.penpal.domain.GetAllStoryResponse
import com.andka.penpal.network.APIConfig

class StoryPaginationViewModel : ViewModel() {
    private val _stories = MutableLiveData<GetAllStoryResponse?>(null)
    val stories: LiveData<GetAllStoryResponse?> = _stories

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    suspend fun getAllStories(token: String, page: Int) {
        _isLoading.postValue(true)
        try {
            val client = APIConfig.getApiService().getStories(token, page, 20)
            _stories.postValue(client)
        } catch (e: Exception) {
            Log.e("STORY VIEW MODEL", e.message!!)
        } finally {
            _isLoading.postValue(false)
        }
    }
}