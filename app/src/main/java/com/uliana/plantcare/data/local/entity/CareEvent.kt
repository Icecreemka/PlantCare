package com.uliana.plantcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

enum class CareEventType {
    WATERING,
    FERTILIZING,
    CHECK,
    REPOT,
    NOTE,
    DRIED_EARLY,
    STILL_MOIST
}

@Entity(tableName = "care_events")
data class CareEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantId: Long,
    val type: CareEventType,
    val date: LocalDate,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val note: String = ""
)
