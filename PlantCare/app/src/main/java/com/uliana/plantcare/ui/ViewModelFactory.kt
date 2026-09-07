package com.uliana.plantcare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.uliana.plantcare.data.repository.PlantRepository
import com.uliana.plantcare.ui.addedit.AddEditPlantViewModel
import com.uliana.plantcare.ui.detail.PlantDetailViewModel
import com.uliana.plantcare.ui.plantlist.PlantListViewModel

class ViewModelFactory(
    private val repository: PlantRepository,
    private val plantId: Long = -1L
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            PlantListViewModel::class.java -> PlantListViewModel(repository) as T
            AddEditPlantViewModel::class.java -> AddEditPlantViewModel(repository) as T
            PlantDetailViewModel::class.java -> PlantDetailViewModel(repository, plantId) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}
