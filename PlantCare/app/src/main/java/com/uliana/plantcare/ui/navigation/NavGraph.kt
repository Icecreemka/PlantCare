package com.uliana.plantcare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uliana.plantcare.data.repository.PlantRepository
import com.uliana.plantcare.ui.ViewModelFactory
import com.uliana.plantcare.ui.addedit.AddEditPlantScreen
import com.uliana.plantcare.ui.addedit.AddEditPlantViewModel
import com.uliana.plantcare.ui.detail.PlantDetailScreen
import com.uliana.plantcare.ui.detail.PlantDetailViewModel
import com.uliana.plantcare.ui.plantlist.PlantListScreen
import com.uliana.plantcare.ui.plantlist.PlantListViewModel

private object Routes {
    const val LIST = "plant_list"
    const val ADD = "add_plant"
    const val DETAIL = "plant_detail/{plantId}"
    fun detail(id: Long) = "plant_detail/$id"
}

@Composable
fun PlantCareNavGraph(repository: PlantRepository) {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.LIST) {
        composable(Routes.LIST) {
            val vm: PlantListViewModel = viewModel(factory = ViewModelFactory(repository))
            PlantListScreen(
                viewModel = vm,
                onAddPlant = { navController.navigate(Routes.ADD) },
                onOpenPlant = { id -> navController.navigate(Routes.detail(id)) }
            )
        }
        composable(Routes.ADD) {
            val vm: AddEditPlantViewModel = viewModel(factory = ViewModelFactory(repository))
            AddEditPlantScreen(
                viewModel = vm,
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("plantId") { type = NavType.LongType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getLong("plantId") ?: return@composable
            val vm: PlantDetailViewModel = viewModel(factory = ViewModelFactory(repository, plantId))
            PlantDetailScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
