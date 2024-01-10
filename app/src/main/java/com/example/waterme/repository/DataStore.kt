package com.example.waterme.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.waterme.model.Plant
import kotlinx.coroutines.flow.first

private const val LAYOUT_PREFERENCES_NAME = "notification_preferences"

val Context.dataStore by preferencesDataStore(name = LAYOUT_PREFERENCES_NAME)

class DataStore(private val dataStore: DataStore<Preferences>) {

    private val IS_ACTIVE = booleanPreferencesKey("is_active")
    private val PLANT_NAME = stringPreferencesKey("plant_name")

    suspend fun saveStateNotification(name: String, state: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ACTIVE] = state
            preferences[PLANT_NAME] = name
        }
    }

    suspend fun returnData() : String {
        val preferences = dataStore.data.first()
        val name = preferences[PLANT_NAME]
        val state = preferences[IS_ACTIVE]
        return ("$name, $state")
    }


    suspend fun returnSwitchState(plantName: String): Boolean {
        return dataStore.data.first()[booleanPreferencesKey(plantName)] ?: false
    }


}
