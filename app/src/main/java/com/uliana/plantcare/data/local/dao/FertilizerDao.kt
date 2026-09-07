package com.uliana.plantcare.data.local.dao

import androidx.room.*
import com.uliana.plantcare.data.local.entity.FertilizerSchedule
import kotlinx.coroutines.flow.Flow

@Dao
interface FertilizerDao {
    @Query("SELECT * FROM fertilizer_schedules WHERE plantId = :plantId AND isActive = 1 LIMIT 1")
    fun observeForPlant(plantId: Long): Flow<FertilizerSchedule?>

    @Query("SELECT * FROM fertilizer_schedules WHERE isActive = 1")
    suspend fun getAllActive(): List<FertilizerSchedule>

    @Insert
    suspend fun insert(schedule: FertilizerSchedule): Long

    @Update
    suspend fun update(schedule: FertilizerSchedule)

    @Delete
    suspend fun delete(schedule: FertilizerSchedule)
}
