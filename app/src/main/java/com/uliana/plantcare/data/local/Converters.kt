package com.uliana.plantcare.data.local

import androidx.room.TypeConverter
import com.uliana.plantcare.data.local.entity.CareEventType
import com.uliana.plantcare.data.local.entity.PlantCategory
import java.time.LocalDate
import java.time.LocalDateTime

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? = value?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? = value?.toString()

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? = value?.let { LocalDateTime.parse(it) }

    @TypeConverter
    fun fromPlantCategory(category: PlantCategory): String = category.name

    @TypeConverter
    fun toPlantCategory(value: String): PlantCategory = PlantCategory.valueOf(value)

    @TypeConverter
    fun fromCareEventType(type: CareEventType): String = type.name

    @TypeConverter
    fun toCareEventType(value: String): CareEventType = CareEventType.valueOf(value)
}
