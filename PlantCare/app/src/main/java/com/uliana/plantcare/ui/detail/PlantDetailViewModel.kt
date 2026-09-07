package com.uliana.plantcare.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uliana.plantcare.data.local.entity.CareEvent
import com.uliana.plantcare.data.local.entity.FertilizerSchedule
import com.uliana.plantcare.data.local.entity.Plant
import com.uliana.plantcare.data.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class PlantDetailViewModel(
    private val repository: PlantRepository,
    private val plantId: Long
) : ViewModel() {

    val plant: StateFlow<Plant?> = repository.observePlant(plantId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val fertilizerSchedule: StateFlow<FertilizerSchedule?> = repository.observeFertilizerSchedule(plantId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val careHistory: StateFlow<List<CareEvent>> = repository.observeCareEvents(plantId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markWatered(latitude: Double?, longitude: Double?) {
        viewModelScope.launch { repository.markWatered(plantId, latitude, longitude) }
    }

    fun reportDryness(driedEarly: Boolean) {
        viewModelScope.launch { repository.reportDrynessFeedback(plantId, driedEarly) }
    }

    fun markChecked() {
        viewModelScope.launch { repository.markChecked(plantId) }
    }

    fun updateRepotDate(date: LocalDate) {
        viewModelScope.launch { repository.updateRepotDate(plantId, date) }
    }

    fun updateWateringNotes(notes: String) {
        viewModelScope.launch { repository.updateWateringNotes(plantId, notes) }
    }

    fun setFertilizerSchedule(intervalDays: Int, fertilizerName: String, notes: String) {
        viewModelScope.launch { repository.setFertilizerSchedule(plantId, intervalDays, fertilizerName, notes) }
    }

    fun markFertilized(schedule: FertilizerSchedule) {
        viewModelScope.launch { repository.markFertilized(schedule) }
    }

    fun deletePlant(onDone: () -> Unit) {
        viewModelScope.launch {
            plant.value?.let { repository.deletePlant(it) }
            onDone()
        }
    }
}
