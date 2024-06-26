package com.andka.hanashi.utils

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.andka.hanashi.data.repository.AuthRepository
import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.data.source.database.StoryDatabase
import com.andka.hanashi.data.source.local.UserPreferences
import com.andka.hanashi.data.source.remote.ApiConfig
import com.andka.hanashi.domain.usecase.GetDetailUseCase
import com.andka.hanashi.domain.usecase.GetStoriesUseCase
import com.andka.hanashi.domain.usecase.GetStoriesWithLocationUseCase
import com.andka.hanashi.domain.usecase.GetUserUseCase
import com.andka.hanashi.domain.usecase.LoginUseCase
import com.andka.hanashi.domain.usecase.LogoutUseCase
import com.andka.hanashi.domain.usecase.NewStoryUseCase
import com.andka.hanashi.domain.usecase.RegisterUseCase
import com.andka.hanashi.ui.detail_story.DetailViewModel
import com.andka.hanashi.ui.homepage.MainActivityViewModel
import com.andka.hanashi.ui.homepage.home.HomeViewModel
import com.andka.hanashi.ui.homepage.maps.MapsViewModel
import com.andka.hanashi.ui.homepage.profile.ProfileViewModel
import com.andka.hanashi.ui.login.LoginViewModel
import com.andka.hanashi.ui.new_story.NewStoryViewModel
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

    // Repositories
    private val userPreferencesRepository by lazy { UserPreferences(requireApplication.dataStore) }
    private val authRepository by lazy { AuthRepository(ApiConfig(requireApplication.dataStore).getApiService()) }
    private val storyRepository by lazy { StoryRepository(ApiConfig(requireApplication.dataStore).getApiService(), StoryDatabase.getDatabase(requireApplication)) }

    // ViewModels
    val loginViewModelFactory get() = LoginViewModel.Factory(LoginUseCase(userPreferencesRepository, authRepository))
    val registerViewModelFactory get() = RegisterViewModel.Factory(RegisterUseCase(authRepository))
    val mainActivityViewModelFactory get() = MainActivityViewModel.Factory(GetUserUseCase(userPreferencesRepository))
    val detailViewModelFactory get() = DetailViewModel.Factory(GetDetailUseCase(storyRepository))
    val homeViewModelFactory get() = HomeViewModel.Factory(GetStoriesUseCase(storyRepository), LogoutUseCase(userPreferencesRepository), GetUserUseCase(userPreferencesRepository))
    val newStoryViewModelFactory get() = NewStoryViewModel.Factory(NewStoryUseCase(storyRepository))
    val profileViewModelFactory get() = ProfileViewModel.Factory(GetUserUseCase(userPreferencesRepository), LogoutUseCase(userPreferencesRepository))
    val mapsViewModelFactory get() = MapsViewModel.Factory(GetStoriesWithLocationUseCase(storyRepository))

}