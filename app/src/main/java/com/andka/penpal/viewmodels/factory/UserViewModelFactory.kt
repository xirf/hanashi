package com.andka.penpal.viewmodels.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andka.penpal.utils.UserPreferences
import com.andka.penpal.viewmodels.UserViewModel

class UserViewModelFactory(private val userPreferences: UserPreferences) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }

}