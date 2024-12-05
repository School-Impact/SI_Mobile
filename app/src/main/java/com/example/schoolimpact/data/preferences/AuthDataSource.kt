package com.example.schoolimpact.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.schoolimpact.data.model.User
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map


val Context.AuthDataStore by preferencesDataStore(name = "auth_prefs")

class AuthDataSource(context: Context) {

    private val authDataStore = context.AuthDataStore

    suspend fun saveUser(user: User) {
        authDataStore.edit { preferences ->
            preferences[USER_NAME_KEY] = user.name
            preferences[USER_TOKEN_KEY] = user.token
        }
    }

    val user = context.AuthDataStore.data.map { preferences ->
        val name = preferences[USER_NAME_KEY]
        val token = preferences[USER_TOKEN_KEY]
        if (name != null && token != null) {
            User(name, token)
        } else {
            null
        }
    }

    suspend fun isLoggedIn(): Boolean {
        val userPreferences = authDataStore.data.firstOrNull()
        return userPreferences?.get(USER_TOKEN_KEY) != null
    }

    suspend fun logout() {
        authDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
    }

}