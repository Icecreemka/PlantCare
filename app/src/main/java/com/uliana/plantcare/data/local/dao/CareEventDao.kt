package com.uliana.plantcare.data.local.dao

import androidx.room.Insert
import androidx.room.Dao
import androidx.room.Query
import com.uliana.plantcare.data.local.entity.CareEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface CareEventDao {
    @Query("SELECT * FROM care_events WHERE plantId = :plantId ORDER BY timestamp DESC")
    fun observeForPlant(plantId: Long): Flow<List<CareEvent>>

    @Insert
    suspend fun insert(event: CareEvent): Long
}
