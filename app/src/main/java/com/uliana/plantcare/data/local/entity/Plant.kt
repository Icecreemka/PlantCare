package com.uliana.plantcare.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class PlantCategory(val displayName: String, val baseEvaporationCoefficient: Double) {
    SUCCULENT_CACTUS("Суккулент / кактус", 0.35),
    TROPICAL_FOLIAGE("Тропическое лиственное", 1.0),
    FLOWERING("Цветущее", 1.15),
    HERB_EDIBLE("Пряная трава / съедобное", 1.3),
    FERN("Папоротник", 1.35),
    ORCHID("Орхидея", 0.6),
    OTHER("Другое", 1.0)
}

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: PlantCategory,
    val potDiameterCm: Double,
    val potHeightCm: Double,
    val photoUri: String? = null,

    val lastRepotDate: LocalDate? = null,
    val wateringNotes: String = "",

    val baseWateringIntervalDays: Double,
    val currentIntervalDays: Double,
    val adjustmentFactor: Double = 1.0,

    val lastWateredDate: LocalDate = LocalDate.now(),
    val nextWateringDate: LocalDate,

    val lastCheckedDate: LocalDate = LocalDate.now(),

    val createdAt: LocalDate = LocalDate.now()
)
