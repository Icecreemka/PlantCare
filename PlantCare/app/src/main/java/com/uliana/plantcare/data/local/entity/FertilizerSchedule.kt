package com.uliana.plantcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "fertilizer_schedules")
data class FertilizerSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val intervalDays: Int,
    val fertilizerName: String = "",
    val lastFertilizedDate: LocalDate = LocalDate.now(),
    val nextFertilizeDate: LocalDate,
    val notes: String = "",
    val isActive: Boolean = true
)
