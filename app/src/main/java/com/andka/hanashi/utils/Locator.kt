package com.andka.hanashi.utils

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.andka.hanashi.data.repository.AuthRepository
import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.data.source.local.UserPreferences
import com.andka.hanashi.data.source.remote.ApiConfig
import com.andka.hanashi.domain.usecase.GetDetailUseCase
import com.andka.hanashi.domain.usecase.GetStoriesUseCase
import com.andka.hanashi.domain.usecase.GetUserUseCase
import com.andka.hanashi.domain.usecase.LoginUseCase
import com.andka.hanashi.domain.usecase.LogoutUseCase
import com.andka.hanashi.domain.usecase.RegisterUseCase
import com.andka.hanashi.ui.detail_story.DetailViewModel
import com.andka.hanashi.ui.homepage.MainActivityViewModel
import com.andka.hanashi.ui.homepage.home.HomeViewModel
import com.andka.hanashi.ui.login.LoginViewModel
import com.andka.hanashi.ui.register.RegisterViewModel

object Locator {
    private var application: Application? = null

    private inline val requireApplication
        get() = application
            ?: error("You forgot to call Locator.initWith(application) in your Application class")

    fun initWith(application: Application) {
        this.application = application
    }

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private val loginUseCase get() = LoginUseCase(userPreferencesRepository, authRepository)
    private val registerUseCase get() = RegisterUseCase(authRepository)
    private val getStoriesUseCase get() = GetStoriesUseCase(storyRepository)
    private val logoutUseCase get() = LogoutUseCase(userPreferencesRepository)
    private val getUserUseCase get() = GetUserUseCase(userPreferencesRepository)
    private val getDetailUseCase get() = GetDetailUseCase(storyRepository)


    val loginViewModelFactory get() = LoginViewModel.Factory(loginUseCase)
    val registerViewModelFactory get() = RegisterViewModel.Factory(registerUseCase)
    val mainActivityViewModelFactory get() = MainActivityViewModel.Factory(getUserUseCase)
    val detailViewModelFactory get() = DetailViewModel.Factory(getDetailUseCase)
    val profileViewModelFactory get() = HomeViewModel.Factory(getUserUseCase, getStoriesUseCase)


    private val userPreferencesRepository by lazy { UserPreferences(requireApplication.dataStore) }
    private val authRepository by lazy { AuthRepository(ApiConfig(requireApplication.dataStore).getApiService()) }
    private val storyRepository by lazy { StoryRepository(ApiConfig(requireApplication.dataStore).getApiService()) }
}