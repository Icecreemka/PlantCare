package com.uliana.plantcare.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uliana.plantcare.data.local.dao.CareEventDao
import com.uliana.plantcare.data.local.dao.FertilizerDao
import com.uliana.plantcare.data.local.dao.PlantDao
import com.uliana.plantcare.data.local.entity.CareEvent
import com.uliana.plantcare.data.local.entity.FertilizerSchedule
import com.uliana.plantcare.data.local.entity.Plant

@Database(
    entities = [Plant::class, FertilizerSchedule::class, CareEvent::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
    abstract fun fertilizerDao(): FertilizerDao
    abstract fun careEventDao(): CareEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plantcare.db"
                )

                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
