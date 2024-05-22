package com.andka.penpal.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val userToken = stringPreferencesKey(Constant.PreferenceProperty.USER_TOKEN.name)
    private val userId = stringPreferencesKey(Constant.PreferenceProperty.USER_ID.name)
    private val userName = stringPreferencesKey(Constant.PreferenceProperty.USER_NAME.name)
    private val userEmail = stringPreferencesKey(Constant.PreferenceProperty.USER_EMAIL.name)


    private val defaultValue = Constant.DEFAULT_VALUE
    private val defaultDate = Constant.DEFAULT_DATE

    fun getUserToken() = dataStore.data.map { pref -> pref[userToken] ?: defaultValue }
    fun getUserId() = dataStore.data.map { pref -> pref[userId] ?: defaultValue }
    fun getUserName() = dataStore.data.map { pref -> pref[userName] ?: defaultValue }
    fun getUserEmail() = dataStore.data.map { pref -> pref[userEmail] ?: defaultValue }

    suspend fun setSession(
        token: String,
        userId: String,
        userName: String,
        userEmail: String,
    ) {
        dataStore.edit { preferences ->
            preferences[this.userId] = userId
            preferences[this.userName] = userName
            preferences[this.userEmail] = userEmail
            preferences[this.userToken] = token
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}