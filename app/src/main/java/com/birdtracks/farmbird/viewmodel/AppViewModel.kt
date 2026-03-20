package com.birdtracks.farmbird.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.birdtracks.farmbird.data.db.AppDatabase
import com.birdtracks.farmbird.data.db.entity.*
import com.birdtracks.farmbird.data.preferences.PreferencesManager
import com.birdtracks.farmbird.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class WildBirdSpecies(
    val name: String,
    val scientificName: String,
    val description: String,
    val migrationSeason: String,
    val migrationRoute: String,
    val habitat: String,
    val category: String,
    val color: String,
    val diet: String,
    val lifespan: String
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.appDao())
    private val prefsManager = PreferencesManager(application)

    // ────── Preferences ──────
    val isOnboardingComplete: StateFlow<Boolean> = prefsManager.isOnboardingComplete
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val farmName: StateFlow<String> = prefsManager.farmName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "My Farm")

    val ownerName: StateFlow<String> = prefsManager.ownerName
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val weightUnit: StateFlow<String> = prefsManager.weightUnit
        .stateIn(viewModelScope, SharingStarted.Eagerly, "kg")

    val temperatureUnit: StateFlow<String> = prefsManager.temperatureUnit
        .stateIn(viewModelScope, SharingStarted.Eagerly, "°C")

    val darkMode: StateFlow<Boolean> = prefsManager.darkMode
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val location: StateFlow<String> = prefsManager.location
        .stateIn(viewModelScope, SharingStarted.Eagerly, "My Location")

    // ────── Wild Birds Catalog ──────
    val wildBirdsCatalog: List<WildBirdSpecies> = listOf(
        WildBirdSpecies("Barn Swallow", "Hirundo rustica", "One of the most widespread swallows in the world. Known for its forked tail and acrobatic flight. Feeds on insects caught in flight.", "Apr – Oct", "Sub-Saharan Africa → Europe/Asia", "Open farmland, meadows, water", "Migratory", "Blue-black back, red-orange throat", "Insects", "4 years"),
        WildBirdSpecies("White Stork", "Ciconia ciconia", "Large wading bird with distinctive white and black plumage. Returns to the same nest every year. Symbol of good luck in many cultures.", "Mar – Aug", "Africa → Europe", "Farmland, wetlands, meadows", "Migratory", "White with black wing tips, red bill", "Frogs, fish, insects", "22 years"),
        WildBirdSpecies("Canada Goose", "Branta canadensis", "Large goose native to North America. Highly adaptable and found in many environments. Known for V-formation flying.", "Oct – Mar (south)", "Canada → Southern USA", "Lakes, rivers, parks, farms", "Waterfowl", "Black head/neck, white cheek patch", "Grass, grains, aquatic plants", "24 years"),
        WildBirdSpecies("Mallard Duck", "Anas platyrhynchos", "The most familiar of all ducks. Males have iridescent green head. Found on almost any body of water. Ancestor of most domestic ducks.", "Year-round", "Northern regions → southern wetlands", "Ponds, rivers, marshes", "Waterfowl", "Male: green head, female: brown", "Seeds, insects, aquatic plants", "10 years"),
        WildBirdSpecies("Common Crane", "Grus grus", "Tall, elegant bird that forms large migratory flocks. Known for its bugling calls and spectacular mating dances.", "Apr – Sep", "Africa/India → Northern Europe", "Wetlands, open fields", "Migratory", "Grey with red crown, black face", "Plants, grain, invertebrates", "40 years"),
        WildBirdSpecies("European Robin", "Erithacus rubecula", "Small passerine with a distinctive orange-red breast. Known for its melodious song. Surprisingly bold and approachable.", "Year-round", "Partial migrant - some move south", "Woodland, gardens, hedgerows", "Songbird", "Orange-red breast, brown back", "Worms, berries, insects", "5 years"),
        WildBirdSpecies("Common Swift", "Apus apus", "Spends almost its entire life in flight, even sleeping on the wing. Screaming calls in summer are iconic. One of the fastest birds in level flight.", "May – Aug", "Sub-Saharan Africa → Europe", "Open sky, near buildings", "Migratory", "All dark brown, pale throat", "Flying insects", "21 years"),
        WildBirdSpecies("House Martin", "Delichon urbicum", "Small migratory bird that builds mud nests under eaves. Often nests in colonies. A reliable sign of spring.", "Apr – Oct", "Sub-Saharan Africa → Europe", "Aerial, near buildings and water", "Migratory", "Blue-black above, white below, white rump", "Flying insects", "5 years"),
        WildBirdSpecies("Eurasian Teal", "Anas crecca", "The smallest dabbling duck. Forms large flocks in winter. Males have chestnut head with green eye patch.", "Oct – Mar", "Northern Europe/Asia → South", "Shallow wetlands, marshes", "Waterfowl", "Male: chestnut/green head, female: brown", "Seeds, aquatic invertebrates", "12 years"),
        WildBirdSpecies("Grey Heron", "Ardea cinerea", "Europe's largest heron. Patient hunter that stands motionless for long periods waiting for fish. Slow, majestic flight.", "Year-round", "Partial migrant in harsh winters", "Rivers, lakes, wetlands", "Wading Bird", "Grey and white with black crest", "Fish, frogs, voles", "25 years"),
        WildBirdSpecies("Lapwing", "Vanellus vanellus", "Distinctive wading bird with iridescent green-black plumage and a crest. Acrobatic display flights in spring. Known for its 'peewit' call.", "Year-round", "Northern Europe → Mediterranean in winter", "Farmland, wetland margins", "Wading Bird", "Green-black back, white below, crest", "Worms, insects, seeds", "5 years"),
        WildBirdSpecies("Red Kite", "Milvus milvus", "Graceful raptor with a distinctive forked tail. Soars effortlessly for hours. Once extinct in Britain, now successfully reintroduced.", "Year-round", "Mainly resident, some movement", "Woodland, farmland, open areas", "Raptor", "Reddish-brown with pale head, forked tail", "Carrion, small mammals, earthworms", "25 years")
    )

    // ────── Observations ──────
    val observations: StateFlow<List<BirdObservationEntity>> = repository.allObservations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentObservations: StateFlow<List<BirdObservationEntity>> = repository.recentObservations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addObservation(species: String, location: String, date: Long, notes: String, count: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addObservation(
                BirdObservationEntity(
                    species = species,
                    location = location,
                    date = date,
                    notes = notes,
                    count = count
                )
            )
        }
    }

    fun deleteObservation(observation: BirdObservationEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteObservation(observation) }
    }

    // ────── Bird Groups ──────
    val birdGroups: StateFlow<List<BirdGroupEntity>> = repository.allBirdGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPoultryCount: StateFlow<Int> = repository.totalPoultryCount
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addBirdGroup(name: String, birdType: String, count: Int, ageWeeks: Int, purpose: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBirdGroup(BirdGroupEntity(name = name, birdType = birdType, count = count, ageWeeks = ageWeeks, purpose = purpose, notes = notes))
        }
    }

    fun updateBirdGroup(group: BirdGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateBirdGroup(group) }
    }

    fun deleteBirdGroup(group: BirdGroupEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBirdGroup(group) }
    }

    // ────── Eggs ──────
    val eggRecords: StateFlow<List<EggRecordEntity>> = repository.allEggRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayEggCount: StateFlow<Int> = repository.getTodayEggCount(
        startOfToday(), endOfToday()
    ).map { it ?: 0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyEggCount: StateFlow<Int> = repository.getWeeklyEggCount(startOfWeek())
        .map { it ?: 0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addEggRecord(groupId: Long, groupName: String, count: Int, date: Long, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEggRecord(EggRecordEntity(groupId = groupId, groupName = groupName, count = count, date = date, notes = notes))
        }
    }

    fun deleteEggRecord(record: EggRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteEggRecord(record) }
    }

    // ────── Feed Records ──────
    val feedRecords: StateFlow<List<FeedRecordEntity>> = repository.allFeedRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentFeedRecords: StateFlow<List<FeedRecordEntity>> = repository.recentFeedRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFeedRecord(feedType: String, amount: Float, unit: String, date: Long, groupName: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFeedRecord(FeedRecordEntity(feedType = feedType, amount = amount, unit = unit, date = date, groupName = groupName, notes = notes))
        }
    }

    fun deleteFeedRecord(record: FeedRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteFeedRecord(record) }
    }

    // ────── Feed Stocks ──────
    val feedStocks: StateFlow<List<FeedStockEntity>> = repository.allFeedStocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFeedStock(name: String, feedType: String, quantity: Float, unit: String, minQuantity: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addFeedStock(FeedStockEntity(name = name, feedType = feedType, quantity = quantity, unit = unit, minQuantity = minQuantity))
        }
    }

    fun updateFeedStock(stock: FeedStockEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateFeedStock(stock) }
    }

    fun deleteFeedStock(stock: FeedStockEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteFeedStock(stock) }
    }

    // ────── Health ──────
    val healthRecords: StateFlow<List<HealthRecordEntity>> = repository.allHealthRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeHealthIssues: StateFlow<Int> = repository.activeHealthIssueCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun addHealthRecord(groupId: Long, groupName: String, issue: String, treatment: String, date: Long, status: String, veterinarian: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addHealthRecord(HealthRecordEntity(groupId = groupId, groupName = groupName, issue = issue, treatment = treatment, date = date, status = status, veterinarian = veterinarian, notes = notes))
        }
    }

    fun updateHealthRecord(record: HealthRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateHealthRecord(record) }
    }

    fun deleteHealthRecord(record: HealthRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteHealthRecord(record) }
    }

    // ────── Breeding ──────
    val breedingRecords: StateFlow<List<BreedingRecordEntity>> = repository.allBreedingRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBreedingRecord(groupId: Long, groupName: String, eggs: Int, temp: Float, startDate: Long, notes: String) {
        val hatchDate = startDate + 21L * 24 * 60 * 60 * 1000
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBreedingRecord(
                BreedingRecordEntity(
                    groupId = groupId,
                    groupName = groupName,
                    incubatorEggs = eggs,
                    temperature = temp,
                    startDate = startDate,
                    expectedHatchDate = hatchDate,
                    status = "Active",
                    notes = notes
                )
            )
        }
    }

    fun updateBreedingRecord(record: BreedingRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateBreedingRecord(record) }
    }

    fun deleteBreedingRecord(record: BreedingRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteBreedingRecord(record) }
    }

    // ────── Tasks ──────
    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTasks: StateFlow<List<TaskEntity>> = repository.upcomingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTask(title: String, description: String, dueDate: Long, category: String, priority: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addTask(TaskEntity(title = title, description = description, dueDate = dueDate, category = category, priority = priority))
        }
    }

    fun toggleTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateTask(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteTask(task) }
    }

    // ────── Equipment ──────
    val equipment: StateFlow<List<EquipmentEntity>> = repository.allEquipment
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addEquipment(name: String, type: String, status: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEquipment(EquipmentEntity(name = name, type = type, status = status, notes = notes))
        }
    }

    fun updateEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.updateEquipment(equipment) }
    }

    fun deleteEquipment(equipment: EquipmentEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteEquipment(equipment) }
    }

    // ────── Costs ──────
    val costRecords: StateFlow<List<CostRecordEntity>> = repository.allCostRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyExpenses: StateFlow<Double> = repository.getMonthlyExpenses(startOfMonth())
        .map { it ?: 0.0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addCostRecord(category: String, amount: Double, description: String, date: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCostRecord(CostRecordEntity(category = category, amount = amount, description = description, date = date))
        }
    }

    fun deleteCostRecord(record: CostRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteCostRecord(record) }
    }

    // ────── Preferences ──────
    fun setOnboardingComplete() {
        viewModelScope.launch { prefsManager.setOnboardingComplete() }
    }

    fun setFarmName(name: String) {
        viewModelScope.launch { prefsManager.setFarmName(name) }
    }

    fun setOwnerName(name: String) {
        viewModelScope.launch { prefsManager.setOwnerName(name) }
    }

    fun setWeightUnit(unit: String) {
        viewModelScope.launch { prefsManager.setWeightUnit(unit) }
    }

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch { prefsManager.setTemperatureUnit(unit) }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefsManager.setDarkMode(enabled) }
    }

    fun setLocation(loc: String) {
        viewModelScope.launch { prefsManager.setLocation(loc) }
    }

    // ────── Helper functions ──────
    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun endOfToday(): Long = startOfToday() + 86_400_000L

    private fun startOfWeek(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun startOfMonth(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
