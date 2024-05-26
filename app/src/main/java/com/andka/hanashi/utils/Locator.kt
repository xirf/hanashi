package com.andka.hanashi.utils

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.andka.hanashi.data.repository.AuthRepository
import com.andka.hanashi.data.repository.StoryRepository
import com.andka.hanashi.data.source.local.UserPreferences
import com.andka.hanashi.data.source.remote.ApiConfig
import com.andka.hanashi.domain.usecase.LoginUseCase
import com.andka.hanashi.domain.usecase.RegisterUseCase
import com.andka.hanashi.ui.login.LoginViewModel
import com.andka.hanashi.ui.register.RegisterViewModel

object Locator {
    private var application: Application? = null

    private inline val requireApplication get() = application ?: error("Missing call: initWith(application)")

    fun initWith(application: Application) { this.application = application }

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    val loginViewModelFactory get() = LoginViewModel.Factory(loginUseCase = loginUseCase)
    val registerViewModelFactory get() = RegisterViewModel.Factory(registerUseCase = registerUseCase)

    // UseCases Injection
    private val loginUseCase get() = LoginUseCase(userPreferencesRepository, authRepository)
    private val registerUseCase get() = RegisterUseCase(authRepository)

    // Repository Injection
    private val userPreferencesRepository by lazy { UserPreferences(requireApplication.dataStore) }
    private val authRepository by lazy { AuthRepository(ApiConfig(requireApplication.dataStore).getApiService()) }
    private val storyRepository by lazy { StoryRepository(ApiConfig(requireApplication.dataStore).getApiService()) }
}