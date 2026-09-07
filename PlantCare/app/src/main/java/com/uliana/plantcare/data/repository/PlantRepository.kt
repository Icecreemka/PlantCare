package com.uliana.plantcare.data.repository

import com.uliana.plantcare.data.local.dao.CareEventDao
import com.uliana.plantcare.data.local.dao.FertilizerDao
import com.uliana.plantcare.data.local.dao.PlantDao
import com.uliana.plantcare.data.local.entity.CareEvent
import com.uliana.plantcare.data.local.entity.CareEventType
import com.uliana.plantcare.data.local.entity.FertilizerSchedule
import com.uliana.plantcare.data.local.entity.Plant
import com.uliana.plantcare.data.local.entity.PlantCategory
import com.uliana.plantcare.domain.WateringCalculator
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class PlantRepository(
    private val plantDao: PlantDao,
    private val fertilizerDao: FertilizerDao,
    private val careEventDao: CareEventDao,
    private val weatherRepository: WeatherRepository
) {

    fun observePlants(): Flow<List<Plant>> = plantDao.observeAll()

    fun observePlant(id: Long): Flow<Plant?> = plantDao.observeById(id)

    fun observeFertilizerSchedule(plantId: Long): Flow<FertilizerSchedule?> =
        fertilizerDao.observeForPlant(plantId)

    fun observeCareEvents(plantId: Long): Flow<List<CareEvent>> =
        careEventDao.observeForPlant(plantId)

    suspend fun addPlant(
        name: String,
        category: PlantCategory,
        potDiameterCm: Double,
        potHeightCm: Double,
        photoUri: String?,
        lastRepotDate: LocalDate?,
        wateringNotes: String,
        latitude: Double?,
        longitude: Double?
    ): Long {
        val baseInterval = WateringCalculator.calculateBaseIntervalDays(potDiameterCm, potHeightCm, category)
        val outlook = if (latitude != null && longitude != null) {
            weatherRepository.getOutlook(latitude, longitude, baseInterval.toInt().coerceAtLeast(1))
        } else null
        val currentInterval = WateringCalculator.effectiveIntervalDays(baseInterval, 1.0, outlook)

        val plant = Plant(
            name = name,
            category = category,
            potDiameterCm = potDiameterCm,
            potHeightCm = potHeightCm,
            photoUri = photoUri,
            lastRepotDate = lastRepotDate,
            wateringNotes = wateringNotes,
            baseWateringIntervalDays = baseInterval,
            currentIntervalDays = currentInterval,
            adjustmentFactor = 1.0,
            lastWateredDate = LocalDate.now(),
            nextWateringDate = LocalDate.now().plusDays(currentInterval.toLong().coerceAtLeast(1))
        )
        return plantDao.insert(plant)
    }

    suspend fun updatePlantDetails(plant: Plant) {

        val recalculatedBase = WateringCalculator.calculateBaseIntervalDays(
            plant.potDiameterCm, plant.potHeightCm, plant.category
        )
        val recalculatedCurrent = recalculatedBase * plant.adjustmentFactor
        plantDao.update(
            plant.copy(
                baseWateringIntervalDays = recalculatedBase,
                currentIntervalDays = recalculatedCurrent
            )
        )
    }

    suspend fun deletePlant(plant: Plant) = plantDao.delete(plant)

    suspend fun markWatered(plantId: Long, latitude: Double?, longitude: Double?) {
        val plant = plantDao.getById(plantId) ?: return
        val outlook = if (latitude != null && longitude != null) {
            weatherRepository.getOutlook(latitude, longitude, plant.currentIntervalDays.toInt().coerceAtLeast(1))
        } else null
        val newInterval = WateringCalculator.effectiveIntervalDays(
            plant.baseWateringIntervalDays, plant.adjustmentFactor, outlook
        )
        val today = LocalDate.now()
        plantDao.update(
            plant.copy(
                lastWateredDate = today,
                nextWateringDate = today.plusDays(newInterval.toLong().coerceAtLeast(1)),
                currentIntervalDays = newInterval,
                lastCheckedDate = today
            )
        )
        careEventDao.insert(CareEvent(plantId = plantId, type = CareEventType.WATERING, date = today))
    }

    suspend fun reportDrynessFeedback(plantId: Long, driedEarly: Boolean) {
        val plant = plantDao.getById(plantId) ?: return
        val newFactor = WateringCalculator.updateAdjustmentFactor(plant.adjustmentFactor, driedEarly)
        val newInterval = plant.baseWateringIntervalDays * newFactor
        val today = LocalDate.now()
        val newNextDate = if (driedEarly) today else plant.lastWateredDate.plusDays(newInterval.toLong().coerceAtLeast(1))

        plantDao.update(
            plant.copy(
                adjustmentFactor = newFactor,
                currentIntervalDays = newInterval,
                nextWateringDate = newNextDate,
                lastCheckedDate = today
            )
        )
        careEventDao.insert(
            CareEvent(
                plantId = plantId,
                type = if (driedEarly) CareEventType.DRIED_EARLY else CareEventType.STILL_MOIST,
                date = today
            )
        )
    }

    suspend fun markChecked(plantId: Long) {
        val plant = plantDao.getById(plantId) ?: return
        plantDao.update(plant.copy(lastCheckedDate = LocalDate.now()))
        careEventDao.insert(CareEvent(plantId = plantId, type = CareEventType.CHECK, date = LocalDate.now()))
    }

    suspend fun updateRepotDate(plantId: Long, date: LocalDate) {
        val plant = plantDao.getById(plantId) ?: return
        plantDao.update(plant.copy(lastRepotDate = date))
        careEventDao.insert(CareEvent(plantId = plantId, type = CareEventType.REPOT, date = date))
    }

    suspend fun updateWateringNotes(plantId: Long, notes: String) {
        val plant = plantDao.getById(plantId) ?: return
        plantDao.update(plant.copy(wateringNotes = notes))
    }

    suspend fun setFertilizerSchedule(plantId: Long, intervalDays: Int, fertilizerName: String, notes: String) {
        val today = LocalDate.now()
        val schedule = FertilizerSchedule(
            plantId = plantId,
            intervalDays = intervalDays,
            fertilizerName = fertilizerName,
            lastFertilizedDate = today,
            nextFertilizeDate = today.plusDays(intervalDays.toLong()),
            notes = notes
        )
        fertilizerDao.insert(schedule)
    }

    suspend fun markFertilized(schedule: FertilizerSchedule) {
        val today = LocalDate.now()
        fertilizerDao.update(
            schedule.copy(
                lastFertilizedDate = today,
                nextFertilizeDate = today.plusDays(schedule.intervalDays.toLong())
            )
        )
        careEventDao.insert(CareEvent(plantId = schedule.plantId, type = CareEventType.FERTILIZING, date = today))
    }

    suspend fun getAllPlantsSnapshot(): List<Plant> = plantDao.getAll()
    suspend fun getAllActiveFertilizerSchedules(): List<FertilizerSchedule> = fertilizerDao.getAllActive()
}
