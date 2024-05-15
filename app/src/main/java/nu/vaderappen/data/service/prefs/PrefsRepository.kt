package nu.vaderappen.data.service.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

import nu.vaderappen.ui.location.Location

val Context.dataStore by preferencesDataStore("weather_preferences")

class PrefsRepository(private val dataStore: DataStore<Preferences>) {

    private val moshi = Moshi.Builder().build()
    private val locationAdapter = moshi.adapter(Location::class.java)

    // Function to save the Location object into DataStore
    suspend fun saveLocation(location: Location) {
        val json = locationAdapter.toJson(location)
        println("Saving location: location")
        dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = json
        }
    }

    suspend fun setShouldUseGPS(should: Boolean) = dataStore.edit {
        it[SHOULD_USE_GPS_KEY] = should
    }

    // Function to fetch the Location object from DataStore
    val location: Flow<Location?> = dataStore.data
        .map { preferences ->
            val json = preferences[LOCATION_KEY]
            json?.let { locationAdapter.fromJson(it) }
        }
        .onEach { println("Prefs, Location: $it") }

    val shouldUseGPS = dataStore.data
        .map { it[SHOULD_USE_GPS_KEY] ?: true }
        .onEach { println("Prefs, should use GPS: $it") }


    companion object {
        private val LOCATION_KEY = stringPreferencesKey("location")
        private val SHOULD_USE_GPS_KEY = booleanPreferencesKey("should_use_gps")
    }

}