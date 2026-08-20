package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.GardenPlantEntity
import com.example.model.GardenSettingsEntity
import com.example.model.PlantCareLogEntity

@Database(
    entities = [
        GardenPlantEntity::class,
        GardenSettingsEntity::class,
        PlantCareLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GardenDatabase : RoomDatabase() {

    abstract fun gardenPlantDao(): GardenPlantDao
    abstract fun gardenSettingsDao(): GardenSettingsDao
    abstract fun plantCareLogDao(): PlantCareLogDao

    companion object {
        @Volatile
        private var INSTANCE: GardenDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE garden_settings ADD COLUMN languageCode TEXT NOT NULL DEFAULT 'en'")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE garden_settings ADD COLUMN lastPlaylistId TEXT NOT NULL DEFAULT 'lofi'")
                database.execSQL("ALTER TABLE garden_settings ADD COLUMN lastTrackId TEXT NOT NULL DEFAULT 'lofi_1'")
                database.execSQL("ALTER TABLE garden_settings ADD COLUMN isAutoMusic INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE garden_settings ADD COLUMN isShuffle INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE garden_settings ADD COLUMN isMusicPlaying INTEGER NOT NULL DEFAULT 1")
            }
        }

        fun getDatabase(context: Context): GardenDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GardenDatabase::class.java,
                    "ambient_garden_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
