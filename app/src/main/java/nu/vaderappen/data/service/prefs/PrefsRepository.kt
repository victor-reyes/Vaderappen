package nu.vaderappen.data.service.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import nu.vaderappen.ui.location.Location

val Context.dataStore by preferencesDataStore("weather_preferences")

class PrefsRepository(private val dataStore: DataStore<Preferences>) {

    private val moshi = Moshi.Builder().build()
    private val locationAdapter = moshi.adapter(Location::class.java)

    // Function to save the Location object into DataStore
    suspend fun saveLocation(location: Location) {
        val json = locationAdapter.toJson(location)
        dataStore.edit { preferences ->
            preferences[LOCATION_KEY] = json
        }
    }

    // Function to fetch the Location object from DataStore
    val location: Flow<Location> = dataStore.data
        .map { preferences ->
            val json = preferences[LOCATION_KEY]
            json?.let { locationAdapter.fromJson(it) } ?: Location(
                "Stockholm",
                "Stockholm",
                59.329323,
                18.068581
            )
        }

    companion object {
        private val LOCATION_KEY = stringPreferencesKey("location")
    }

}