package com.birdtracks.farmbird.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bird_observations")
data class BirdObservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val species: String,
    val location: String,
    val date: Long,
    val notes: String = "",
    val count: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bird_groups")
data class BirdGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val birdType: String,
    val count: Int,
    val ageWeeks: Int,
    val purpose: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "egg_records")
data class EggRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val groupName: String,
    val count: Int,
    val date: Long,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feed_records")
data class FeedRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val feedType: String,
    val amount: Float,
    val unit: String,
    val date: Long,
    val groupName: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feed_stocks")
data class FeedStockEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val feedType: String,
    val quantity: Float,
    val unit: String,
    val minQuantity: Float = 5f,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_records")
data class HealthRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val groupName: String,
    val issue: String,
    val treatment: String,
    val date: Long,
    val status: String,
    val veterinarian: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "breeding_records")
data class BreedingRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val groupName: String,
    val incubatorEggs: Int,
    val temperature: Float,
    val startDate: Long,
    val expectedHatchDate: Long,
    val status: String,
    val actualHatched: Int = 0,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val category: String,
    val priority: String = "Normal",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    val status: String,
    val notes: String = "",
    val purchaseDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cost_records")
data class CostRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val amount: Double,
    val description: String,
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)
