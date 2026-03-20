package com.birdtracks.farmbird.data.db.dao

import androidx.room.*
import com.birdtracks.farmbird.data.db.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Bird Observations
    @Query("SELECT * FROM bird_observations ORDER BY date DESC")
    fun getAllObservations(): Flow<List<BirdObservationEntity>>

    @Query("SELECT * FROM bird_observations ORDER BY date DESC LIMIT 5")
    fun getRecentObservations(): Flow<List<BirdObservationEntity>>

    @Query("SELECT COUNT(*) FROM bird_observations WHERE date >= :startOfDay AND date < :endOfDay")
    fun getTodayObservationCount(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservation(observation: BirdObservationEntity): Long

    @Delete
    suspend fun deleteObservation(observation: BirdObservationEntity)

    // Bird Groups
    @Query("SELECT * FROM bird_groups ORDER BY createdAt DESC")
    fun getAllBirdGroups(): Flow<List<BirdGroupEntity>>

    @Query("SELECT * FROM bird_groups WHERE id = :id")
    suspend fun getBirdGroupById(id: Long): BirdGroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBirdGroup(group: BirdGroupEntity): Long

    @Update
    suspend fun updateBirdGroup(group: BirdGroupEntity)

    @Delete
    suspend fun deleteBirdGroup(group: BirdGroupEntity)

    @Query("SELECT SUM(count) FROM bird_groups")
    fun getTotalPoultryCount(): Flow<Int?>

    // Egg Records
    @Query("SELECT * FROM egg_records ORDER BY date DESC")
    fun getAllEggRecords(): Flow<List<EggRecordEntity>>

    @Query("SELECT * FROM egg_records WHERE date >= :startOfDay AND date < :endOfDay")
    fun getTodayEggRecords(startOfDay: Long, endOfDay: Long): Flow<List<EggRecordEntity>>

    @Query("SELECT SUM(count) FROM egg_records WHERE date >= :startOfDay AND date < :endOfDay")
    fun getTodayEggCount(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("SELECT SUM(count) FROM egg_records WHERE date >= :startOfWeek")
    fun getWeeklyEggCount(startOfWeek: Long): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEggRecord(record: EggRecordEntity): Long

    @Delete
    suspend fun deleteEggRecord(record: EggRecordEntity)

    // Feed Records
    @Query("SELECT * FROM feed_records ORDER BY date DESC")
    fun getAllFeedRecords(): Flow<List<FeedRecordEntity>>

    @Query("SELECT * FROM feed_records ORDER BY date DESC LIMIT 10")
    fun getRecentFeedRecords(): Flow<List<FeedRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedRecord(record: FeedRecordEntity): Long

    @Delete
    suspend fun deleteFeedRecord(record: FeedRecordEntity)

    // Feed Stocks
    @Query("SELECT * FROM feed_stocks ORDER BY name ASC")
    fun getAllFeedStocks(): Flow<List<FeedStockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedStock(stock: FeedStockEntity): Long

    @Update
    suspend fun updateFeedStock(stock: FeedStockEntity)

    @Delete
    suspend fun deleteFeedStock(stock: FeedStockEntity)

    // Health Records
    @Query("SELECT * FROM health_records ORDER BY date DESC")
    fun getAllHealthRecords(): Flow<List<HealthRecordEntity>>

    @Query("SELECT COUNT(*) FROM health_records WHERE status = 'Active'")
    fun getActiveHealthIssueCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthRecordEntity): Long

    @Update
    suspend fun updateHealthRecord(record: HealthRecordEntity)

    @Delete
    suspend fun deleteHealthRecord(record: HealthRecordEntity)

    // Breeding Records
    @Query("SELECT * FROM breeding_records ORDER BY startDate DESC")
    fun getAllBreedingRecords(): Flow<List<BreedingRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreedingRecord(record: BreedingRecordEntity): Long

    @Update
    suspend fun updateBreedingRecord(record: BreedingRecordEntity)

    @Delete
    suspend fun deleteBreedingRecord(record: BreedingRecordEntity)

    // Tasks
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY dueDate ASC LIMIT 5")
    fun getUpcomingTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // Equipment
    @Query("SELECT * FROM equipment ORDER BY name ASC")
    fun getAllEquipment(): Flow<List<EquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: EquipmentEntity): Long

    @Update
    suspend fun updateEquipment(equipment: EquipmentEntity)

    @Delete
    suspend fun deleteEquipment(equipment: EquipmentEntity)

    // Cost Records
    @Query("SELECT * FROM cost_records ORDER BY date DESC")
    fun getAllCostRecords(): Flow<List<CostRecordEntity>>

    @Query("SELECT SUM(amount) FROM cost_records WHERE date >= :startOfMonth")
    fun getMonthlyExpenses(startOfMonth: Long): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCostRecord(record: CostRecordEntity): Long

    @Delete
    suspend fun deleteCostRecord(record: CostRecordEntity)
}
