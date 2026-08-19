package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.GardenPlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenPlantDao {

    @Query("SELECT * FROM garden_plants ORDER BY slotIndex ASC")
    fun getAllPlants(): Flow<List<GardenPlantEntity>>

    @Query("SELECT * FROM garden_plants WHERE slotIndex = :slotIndex LIMIT 1")
    fun getPlantBySlot(slotIndex: Int): Flow<GardenPlantEntity?>

    @Query("SELECT * FROM garden_plants WHERE id = :id LIMIT 1")
    suspend fun getPlantById(id: Long): GardenPlantEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: GardenPlantEntity): Long

    @Update
    suspend fun updatePlant(plant: GardenPlantEntity)

    @Query(
        """
        UPDATE garden_plants 
        SET lastWateredTimestamp = :wateredTime, 
            totalWaterCount = totalWaterCount + 1,
            hydrationLevel = :hydrationLevel,
            healthStatus = :healthStatus,
            healthScore = :healthScore
        WHERE id = :plantId
        """
    )
    suspend fun waterPlant(
        plantId: Long,
        wateredTime: Long,
        hydrationLevel: Float = 1.0f,
        healthStatus: String = "THRIVING",
        healthScore: Float = 1.0f
    )

    @Query(
        """
        UPDATE garden_plants 
        SET hydrationLevel = :hydrationLevel,
            healthStatus = :healthStatus,
            healthScore = :healthScore,
            currentStage = :currentStage
        WHERE id = :plantId
        """
    )
    suspend fun updatePlantHealthAndStage(
        plantId: Long,
        hydrationLevel: Float,
        healthStatus: String,
        healthScore: Float,
        currentStage: Int
    )

    @Query("DELETE FROM garden_plants WHERE id = :id")
    suspend fun deletePlantById(id: Long)

    @Query("DELETE FROM garden_plants WHERE slotIndex = :slotIndex")
    suspend fun deletePlantBySlot(slotIndex: Int)
}
