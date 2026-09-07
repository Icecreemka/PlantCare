package com.uliana.plantcare.ui.plantlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uliana.plantcare.data.local.entity.Plant
import com.uliana.plantcare.data.repository.PlantRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantListViewModel(private val repository: PlantRepository) : ViewModel() {

    val plants: StateFlow<List<Plant>> = repository.observePlants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun markWatered(plantId: Long, latitude: Double?, longitude: Double?) {
        viewModelScope.launch { repository.markWatered(plantId, latitude, longitude) }
    }
}
