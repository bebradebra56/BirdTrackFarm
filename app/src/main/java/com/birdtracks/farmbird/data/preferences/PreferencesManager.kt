package com.birdtracks.farmbird.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bird_track_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val FARM_NAME = stringPreferencesKey("farm_name")
        val OWNER_NAME = stringPreferencesKey("owner_name")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LOCATION = stringPreferencesKey("location")
    }

    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETE] ?: false
    }

    val farmName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[FARM_NAME] ?: "My Farm"
    }

    val ownerName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[OWNER_NAME] ?: ""
    }

    val weightUnit: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[WEIGHT_UNIT] ?: "kg"
    }

    val temperatureUnit: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[TEMPERATURE_UNIT] ?: "°C"
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE] ?: false
    }

    val location: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[LOCATION] ?: "My Location"
    }

    suspend fun setOnboardingComplete() {
        context.dataStore.edit { prefs -> prefs[ONBOARDING_COMPLETE] = true }
    }

    suspend fun setFarmName(name: String) {
        context.dataStore.edit { prefs -> prefs[FARM_NAME] = name }
    }

    suspend fun setOwnerName(name: String) {
        context.dataStore.edit { prefs -> prefs[OWNER_NAME] = name }
    }

    suspend fun setWeightUnit(unit: String) {
        context.dataStore.edit { prefs -> prefs[WEIGHT_UNIT] = unit }
    }

    suspend fun setTemperatureUnit(unit: String) {
        context.dataStore.edit { prefs -> prefs[TEMPERATURE_UNIT] = unit }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[DARK_MODE] = enabled }
    }

    suspend fun setLocation(loc: String) {
        context.dataStore.edit { prefs -> prefs[LOCATION] = loc }
    }
}
