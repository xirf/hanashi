package com.andka.penpal.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.andka.penpal.utils.Constant
import com.andka.penpal.utils.UserPreferences
import kotlinx.coroutines.launch

class UserViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun getUserPreferences(property: String): LiveData<String> {
        return when (property) {
            Constant.PreferenceProperty.USER_ID.name -> userPreferences.getUserId().asLiveData()
            Constant.PreferenceProperty.USER_NAME.name -> userPreferences.getUserName().asLiveData()
            Constant.PreferenceProperty.USER_EMAIL.name -> userPreferences.getUserEmail().asLiveData()
            Constant.PreferenceProperty.USER_TOKEN.name -> userPreferences.getUserToken().asLiveData()
            Constant.PreferenceProperty.USER_LAST_LOGIN.name -> userPreferences.getUserLastLogin().asLiveData()
            else -> throw IllegalArgumentException("Invalid property name")
        }
    }

    fun setUserPreferences(
        token: String,
        userId: String,
        userName: String,
        userEmail: String
    ) {
        viewModelScope.launch {
            userPreferences.setSession(token, userId, userName, userEmail)
        }
    }

    fun clearUserPreferences() {
        viewModelScope.launch {
            userPreferences.clearSession()
        }
    }
}