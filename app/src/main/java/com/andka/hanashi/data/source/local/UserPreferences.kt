package com.andka.hanashi.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.andka.hanashi.domain.entity.UserEntity
import com.andka.hanashi.domain.interfaces.UserPreferencesRepositoryInterface
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) :
    UserPreferencesRepositoryInterface {
    private object Keys {
        val ID = stringPreferencesKey("id")
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val TOKEN = stringPreferencesKey("token")
    }

    private inline val Preferences.id get() = this[Keys.ID] ?: ""
    private inline val Preferences.name get() = this[Keys.NAME] ?: ""
    private inline val Preferences.email get() = this[Keys.EMAIL] ?: ""
    private inline val Preferences.token get() = this[Keys.TOKEN] ?: ""

    override val getUserData = dataStore.data.map { preferences ->
        UserEntity(
            id = preferences.id,
            name = preferences.name,
            token = preferences.token
        )
    }

    override suspend fun saveUserData(userEntity: UserEntity) {
        dataStore.edit { preferences ->
            preferences[Keys.ID] = userEntity.id
            preferences[Keys.NAME] = userEntity.name
            preferences[Keys.TOKEN] = userEntity.token
        }
    }

    override suspend fun clearUserData() {
        dataStore.edit { it.clear() }
    }

}