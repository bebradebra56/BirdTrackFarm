package com.birdtracks.farmbird.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.birdtracks.farmbird.ui.screens.*
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController, viewModel = viewModel)
        }

        // Wild Birds
        composable(Screen.WildBirds.route) {
            WildBirdsScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.BirdDetail.route,
            arguments = listOf(navArgument("birdName") { type = NavType.StringType })
        ) { backStackEntry ->
            val birdName = backStackEntry.arguments?.getString("birdName") ?: ""
            BirdDetailScreen(navController = navController, viewModel = viewModel, birdName = birdName)
        }

        composable(Screen.MigrationMap.route) {
            MigrationMapScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.AddObservation.route + "?species={species}",
            arguments = listOf(navArgument("species") { type = NavType.StringType; defaultValue = "" })
        ) { backStackEntry ->
            val preselectedSpecies = backStackEntry.arguments?.getString("species") ?: ""
            AddObservationScreen(navController = navController, viewModel = viewModel, preselectedSpecies = preselectedSpecies)
        }

        composable(Screen.AddObservation.route) {
            AddObservationScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.ObservationHistory.route) {
            ObservationHistoryScreen(navController = navController, viewModel = viewModel)
        }

        // Poultry
        composable(Screen.Poultry.route) {
            PoultryScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.BirdGroups.route) {
            BirdGroupsScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.AddBirdGroup.route,
            arguments = listOf(navArgument("groupId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getLong("groupId") ?: -1L
            AddBirdGroupScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.EggTracker.route) {
            EggTrackerScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.AddEggRecord.route) {
            AddEggRecordScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Feeding.route) {
            FeedingScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.AddFeedRecord.route) {
            AddFeedRecordScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.FeedStorage.route) {
            FeedStorageScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Health.route) {
            HealthScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.AddHealthRecord.route) {
            AddHealthRecordScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Breeding.route) {
            BreedingScreen(navController = navController, viewModel = viewModel)
        }

        composable(
            route = Screen.Incubator.route,
            arguments = listOf(navArgument("recordId") { type = NavType.LongType; defaultValue = -1L })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getLong("recordId") ?: -1L
            IncubatorScreen(navController = navController, viewModel = viewModel, recordId = recordId)
        }

        composable(Screen.Chicks.route) {
            ChicksScreen(navController = navController, viewModel = viewModel)
        }

        // Farm tools
        composable(Screen.Calendar.route) {
            CalendarScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Tasks.route) {
            TasksScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Equipment.route) {
            EquipmentScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Costs.route) {
            CostsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Reports.route) {
            ReportsScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.ActivityHistory.route) {
            ActivityHistoryScreen(navController = navController, viewModel = viewModel)
        }

        // Settings
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, viewModel = viewModel)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController, viewModel = viewModel)
        }
    }
}
