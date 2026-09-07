package com.uliana.plantcare.ui.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uliana.plantcare.data.local.entity.PlantCategory
import com.uliana.plantcare.data.repository.PlantRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddEditPlantViewModel(private val repository: PlantRepository) : ViewModel() {

    fun addPlant(
        name: String,
        category: PlantCategory,
        diameterCm: Double,
        heightCm: Double,
        photoUri: String?,
        lastRepotDate: LocalDate?,
        wateringNotes: String,
        latitude: Double?,
        longitude: Double?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addPlant(
                name = name,
                category = category,
                potDiameterCm = diameterCm,
                potHeightCm = heightCm,
                photoUri = photoUri,
                lastRepotDate = lastRepotDate,
                wateringNotes = wateringNotes,
                latitude = latitude,
                longitude = longitude
            )
            onDone()
        }
    }
}
