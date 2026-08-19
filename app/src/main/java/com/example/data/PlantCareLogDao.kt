package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.PlantCareLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantCareLogDao {

    @Query("SELECT * FROM plant_care_logs WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun getLogsForPlant(plantId: Long): Flow<List<PlantCareLogEntity>>

    @Query("SELECT * FROM plant_care_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 30): Flow<List<PlantCareLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PlantCareLogEntity): Long

    @Query("DELETE FROM plant_care_logs WHERE plantId = :plantId")
    suspend fun deleteLogsForPlant(plantId: Long)
}
