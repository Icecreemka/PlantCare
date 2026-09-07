package com.uliana.plantcare.data.local.dao

import androidx.room.*
import com.uliana.plantcare.data.local.entity.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY nextWateringDate ASC")
    fun observeAll(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id")
    fun observeById(id: Long): Flow<Plant?>

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getById(id: Long): Plant?

    @Query("SELECT * FROM plants")
    suspend fun getAll(): List<Plant>

    @Insert
    suspend fun insert(plant: Plant): Long

    @Update
    suspend fun update(plant: Plant)

    @Delete
    suspend fun delete(plant: Plant)
}
