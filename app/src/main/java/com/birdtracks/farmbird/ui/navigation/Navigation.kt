package com.birdtracks.farmbird.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Dashboard : Screen("dashboard")

    // Wild Birds
    data object WildBirds : Screen("wild_birds")
    data object BirdDetail : Screen("bird_detail/{birdName}") {
        fun createRoute(birdName: String) = "bird_detail/${birdName}"
    }
    data object MigrationMap : Screen("migration_map")
    data object AddObservation : Screen("add_observation")
    data object ObservationHistory : Screen("observation_history")

    // Poultry
    data object Poultry : Screen("poultry")
    data object BirdGroups : Screen("bird_groups")
    data object AddBirdGroup : Screen("add_bird_group/{groupId}") {
        fun createRoute(groupId: Long = -1L) = "add_bird_group/$groupId"
    }
    data object EggTracker : Screen("egg_tracker")
    data object AddEggRecord : Screen("add_egg_record")
    data object Feeding : Screen("feeding")
    data object AddFeedRecord : Screen("add_feed_record")
    data object FeedStorage : Screen("feed_storage")
    data object Health : Screen("health")
    data object AddHealthRecord : Screen("add_health_record")
    data object Breeding : Screen("breeding")
    data object Incubator : Screen("incubator/{recordId}") {
        fun createRoute(recordId: Long = -1L) = "incubator/$recordId"
    }
    data object Chicks : Screen("chicks")

    // Farm Tools
    data object Calendar : Screen("calendar")
    data object Tasks : Screen("tasks")
    data object Equipment : Screen("equipment")
    data object Costs : Screen("costs")
    data object Reports : Screen("reports")
    data object ActivityHistory : Screen("activity_history")

    // Settings
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard.route, "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    BottomNavItem(Screen.WildBirds.route, "Wild Birds", Icons.Filled.NaturePeople, Icons.Outlined.NaturePeople),
    BottomNavItem(Screen.Poultry.route, "Poultry", Icons.Filled.Egg, Icons.Outlined.Egg),
    BottomNavItem(Screen.Reports.route, "Reports", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    BottomNavItem(Screen.Settings.route, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

val screensWithoutBottomBar = setOf(
    Screen.Splash.route,
    Screen.Onboarding.route
)

val screensWithBackButton = setOf(
    Screen.BirdDetail.route,
    Screen.MigrationMap.route,
    Screen.AddObservation.route,
    Screen.ObservationHistory.route,
    Screen.BirdGroups.route,
    Screen.AddBirdGroup.route,
    Screen.EggTracker.route,
    Screen.AddEggRecord.route,
    Screen.Feeding.route,
    Screen.AddFeedRecord.route,
    Screen.FeedStorage.route,
    Screen.Health.route,
    Screen.AddHealthRecord.route,
    Screen.Breeding.route,
    Screen.Incubator.route,
    Screen.Chicks.route,
    Screen.Calendar.route,
    Screen.Tasks.route,
    Screen.Equipment.route,
    Screen.Costs.route,
    Screen.ActivityHistory.route,
    Screen.Profile.route
)

fun getScreenTitle(route: String?): String = when {
    route == null -> ""
    route == Screen.Dashboard.route -> "Dashboard"
    route == Screen.WildBirds.route -> "Wild Birds"
    route.startsWith("bird_detail") -> "Bird Details"
    route == Screen.MigrationMap.route -> "Migration Map"
    route == Screen.AddObservation.route -> "Add Observation"
    route == Screen.ObservationHistory.route -> "Observation Journal"
    route == Screen.Poultry.route -> "Poultry"
    route == Screen.BirdGroups.route -> "Bird Groups"
    route.startsWith("add_bird_group") -> "Add Bird Group"
    route == Screen.EggTracker.route -> "Egg Tracker"
    route == Screen.AddEggRecord.route -> "Log Eggs"
    route == Screen.Feeding.route -> "Feeding"
    route == Screen.AddFeedRecord.route -> "Log Feeding"
    route == Screen.FeedStorage.route -> "Feed Storage"
    route == Screen.Health.route -> "Health Records"
    route == Screen.AddHealthRecord.route -> "Health Record"
    route == Screen.Breeding.route -> "Breeding"
    route.startsWith("incubator") -> "Incubator"
    route == Screen.Chicks.route -> "Chicks"
    route == Screen.Calendar.route -> "Calendar"
    route == Screen.Tasks.route -> "Tasks"
    route == Screen.Equipment.route -> "Equipment"
    route == Screen.Costs.route -> "Costs"
    route == Screen.Reports.route -> "Reports"
    route == Screen.ActivityHistory.route -> "Activity History"
    route == Screen.Profile.route -> "Profile"
    route == Screen.Settings.route -> "Settings"
    else -> "BirdTrack Farm"
}
