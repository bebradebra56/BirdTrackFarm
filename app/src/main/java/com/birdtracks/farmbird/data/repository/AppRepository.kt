package com.birdtracks.farmbird.data.repository

import com.birdtracks.farmbird.data.db.dao.AppDao
import com.birdtracks.farmbird.data.db.entity.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {

    // Observations
    val allObservations: Flow<List<BirdObservationEntity>> = dao.getAllObservations()
    val recentObservations: Flow<List<BirdObservationEntity>> = dao.getRecentObservations()

    suspend fun addObservation(observation: BirdObservationEntity) = dao.insertObservation(observation)
    suspend fun deleteObservation(observation: BirdObservationEntity) = dao.deleteObservation(observation)

    // Bird Groups
    val allBirdGroups: Flow<List<BirdGroupEntity>> = dao.getAllBirdGroups()
    val totalPoultryCount: Flow<Int?> = dao.getTotalPoultryCount()

    suspend fun getBirdGroupById(id: Long): BirdGroupEntity? = dao.getBirdGroupById(id)
    suspend fun addBirdGroup(group: BirdGroupEntity) = dao.insertBirdGroup(group)
    suspend fun updateBirdGroup(group: BirdGroupEntity) = dao.updateBirdGroup(group)
    suspend fun deleteBirdGroup(group: BirdGroupEntity) = dao.deleteBirdGroup(group)

    // Eggs
    val allEggRecords: Flow<List<EggRecordEntity>> = dao.getAllEggRecords()

    fun getTodayEggCount(startOfDay: Long, endOfDay: Long): Flow<Int?> =
        dao.getTodayEggCount(startOfDay, endOfDay)

    fun getWeeklyEggCount(startOfWeek: Long): Flow<Int?> = dao.getWeeklyEggCount(startOfWeek)

    suspend fun addEggRecord(record: EggRecordEntity) = dao.insertEggRecord(record)
    suspend fun deleteEggRecord(record: EggRecordEntity) = dao.deleteEggRecord(record)

    // Feed Records
    val allFeedRecords: Flow<List<FeedRecordEntity>> = dao.getAllFeedRecords()
    val recentFeedRecords: Flow<List<FeedRecordEntity>> = dao.getRecentFeedRecords()

    suspend fun addFeedRecord(record: FeedRecordEntity) = dao.insertFeedRecord(record)
    suspend fun deleteFeedRecord(record: FeedRecordEntity) = dao.deleteFeedRecord(record)

    // Feed Stocks
    val allFeedStocks: Flow<List<FeedStockEntity>> = dao.getAllFeedStocks()

    suspend fun addFeedStock(stock: FeedStockEntity) = dao.insertFeedStock(stock)
    suspend fun updateFeedStock(stock: FeedStockEntity) = dao.updateFeedStock(stock)
    suspend fun deleteFeedStock(stock: FeedStockEntity) = dao.deleteFeedStock(stock)

    // Health
    val allHealthRecords: Flow<List<HealthRecordEntity>> = dao.getAllHealthRecords()
    val activeHealthIssueCount: Flow<Int> = dao.getActiveHealthIssueCount()

    suspend fun addHealthRecord(record: HealthRecordEntity) = dao.insertHealthRecord(record)
    suspend fun updateHealthRecord(record: HealthRecordEntity) = dao.updateHealthRecord(record)
    suspend fun deleteHealthRecord(record: HealthRecordEntity) = dao.deleteHealthRecord(record)

    // Breeding
    val allBreedingRecords: Flow<List<BreedingRecordEntity>> = dao.getAllBreedingRecords()

    suspend fun addBreedingRecord(record: BreedingRecordEntity) = dao.insertBreedingRecord(record)
    suspend fun updateBreedingRecord(record: BreedingRecordEntity) = dao.updateBreedingRecord(record)
    suspend fun deleteBreedingRecord(record: BreedingRecordEntity) = dao.deleteBreedingRecord(record)

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val upcomingTasks: Flow<List<TaskEntity>> = dao.getUpcomingTasks()

    suspend fun addTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

    // Equipment
    val allEquipment: Flow<List<EquipmentEntity>> = dao.getAllEquipment()

    suspend fun addEquipment(equipment: EquipmentEntity) = dao.insertEquipment(equipment)
    suspend fun updateEquipment(equipment: EquipmentEntity) = dao.updateEquipment(equipment)
    suspend fun deleteEquipment(equipment: EquipmentEntity) = dao.deleteEquipment(equipment)

    // Costs
    val allCostRecords: Flow<List<CostRecordEntity>> = dao.getAllCostRecords()

    fun getMonthlyExpenses(startOfMonth: Long): Flow<Double?> = dao.getMonthlyExpenses(startOfMonth)

    suspend fun addCostRecord(record: CostRecordEntity) = dao.insertCostRecord(record)
    suspend fun deleteCostRecord(record: CostRecordEntity) = dao.deleteCostRecord(record)
}
