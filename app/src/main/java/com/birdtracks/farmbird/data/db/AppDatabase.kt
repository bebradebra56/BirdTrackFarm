package com.birdtracks.farmbird.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.birdtracks.farmbird.data.db.dao.AppDao
import com.birdtracks.farmbird.data.db.entity.*

@Database(
    entities = [
        BirdObservationEntity::class,
        BirdGroupEntity::class,
        EggRecordEntity::class,
        FeedRecordEntity::class,
        FeedStockEntity::class,
        HealthRecordEntity::class,
        BreedingRecordEntity::class,
        TaskEntity::class,
        EquipmentEntity::class,
        CostRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bird_track_farm_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
