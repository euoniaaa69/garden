package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.GardenPlantEntity
import com.example.model.GardenSettingsEntity
import com.example.model.PlantCareLogEntity

@Database(
    entities = [
        GardenPlantEntity::class,
        GardenSettingsEntity::class,
        PlantCareLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GardenDatabase : RoomDatabase() {

    abstract fun gardenPlantDao(): GardenPlantDao
    abstract fun gardenSettingsDao(): GardenSettingsDao
    abstract fun plantCareLogDao(): PlantCareLogDao

    companion object {
        @Volatile
        private var INSTANCE: GardenDatabase? = null

        fun getDatabase(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "ambient_garden_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
