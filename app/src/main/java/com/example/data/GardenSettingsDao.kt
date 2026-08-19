package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.GardenSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenSettingsDao {

    @Query("SELECT * FROM garden_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<GardenSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: GardenSettingsEntity)

    @Update
    suspend fun updateSettings(settings: GardenSettingsEntity)
}
