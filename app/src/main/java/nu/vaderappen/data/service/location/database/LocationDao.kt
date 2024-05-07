package nu.vaderappen.data.service.location.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import nu.vaderappen.ui.location.Location

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: Location)

    @Update
    suspend fun update(location: Location)

    @Query("SELECT * FROM Location")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM Location WHERE isFaved = 1")
    fun getFavedLocations(): Flow<List<Location>>

    @Delete
    suspend fun deleteLocations(vararg locations: Location)
}


