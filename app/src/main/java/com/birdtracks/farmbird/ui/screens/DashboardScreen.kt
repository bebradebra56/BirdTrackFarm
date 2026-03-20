package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(navController: NavController, viewModel: AppViewModel) {
    val farmName by viewModel.farmName.collectAsState()
    val ownerName by viewModel.ownerName.collectAsState()
    val recentObservations by viewModel.recentObservations.collectAsState()
    val totalPoultry by viewModel.totalPoultryCount.collectAsState()
    val todayEggs by viewModel.todayEggCount.collectAsState()
    val weeklyEggs by viewModel.weeklyEggCount.collectAsState()
    val upcomingTasks by viewModel.upcomingTasks.collectAsState()
    val feedStocks by viewModel.feedStocks.collectAsState()
    val activeHealth by viewModel.activeHealthIssues.collectAsState()

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val dateStr = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.ENGLISH).format(Date())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF1B5E20), Color(0xFF2D6A4F), MaterialTheme.colorScheme.background)
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (ownerName.isNotEmpty()) "$greeting, $ownerName!" else greeting,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = farmName,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .clickable { navController.navigate(Screen.Profile.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Person, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        // Stat Cards
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Wild Birds",
                        value = "${recentObservations.sumOf { it.count }}",
                        subtitle = "Recent sightings",
                        icon = Icons.Filled.NaturePeople,
                        containerColor = Color(0xFFE8F5E9),
                        contentColor = Color(0xFF1B5E20),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Poultry",
                        value = "$totalPoultry",
                        subtitle = "Total birds",
                        icon = Icons.Filled.Egg,
                        containerColor = Color(0xFFE3F2FD),
                        contentColor = Color(0xFF0D47A1),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Eggs Today",
                        value = "$todayEggs",
                        subtitle = "$weeklyEggs this week",
                        icon = Icons.Filled.Circle,
                        containerColor = Color(0xFFFFF8E1),
                        contentColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Feed Stocks",
                        value = "${feedStocks.size}",
                        subtitle = "${feedStocks.count { it.quantity > it.minQuantity }} well-stocked",
                        icon = Icons.Filled.Inventory,
                        containerColor = if (activeHealth > 0) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (activeHealth > 0) Color(0xFFC62828) else MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("Quick Actions")
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val actions = listOf(
                        Triple("Log Eggs", Icons.Filled.Add, Screen.AddEggRecord.route),
                        Triple("Add Feeding", Icons.Filled.Restaurant, Screen.AddFeedRecord.route),
                        Triple("Wild Bird", Icons.Filled.Search, Screen.AddObservation.route),
                        Triple("Health Log", Icons.Filled.HealthAndSafety, Screen.AddHealthRecord.route),
                        Triple("New Task", Icons.Filled.TaskAlt, Screen.Tasks.route),
                        Triple("Costs", Icons.Filled.AttachMoney, Screen.Costs.route)
                    )
                    items(actions) { (label, icon, route) ->
                        QuickActionChip(label = label, icon = icon, onClick = { navController.navigate(route) })
                    }
                }
            }
        }

        // Health Alert
        if (activeHealth > 0) {
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Filled.Warning, null, tint = Color(0xFFC62828), modifier = Modifier.size(24.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Health Alert", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                            Text("$activeHealth active health issue(s) need attention", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7B0000))
                        }
                        TextButton(onClick = { navController.navigate(Screen.Health.route) }) {
                            Text("View", color = Color(0xFFC62828))
                        }
                    }
                }
            }
        }

        // Upcoming Tasks
        if (upcomingTasks.isNotEmpty()) {
            item {
                SectionCard(
                    title = "Upcoming Tasks",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    trailingAction = {
                        TextButton(onClick = { navController.navigate(Screen.Tasks.route) }) { Text("See all") }
                    }
                ) {
                    upcomingTasks.take(3).forEach { task ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = getCategoryIcon(task.category),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(Modifier.weight(1f)) {
                                Text(task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(formatShortDate(task.dueDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            TagChip(
                                label = task.priority,
                                color = if (task.priority == "High") Color(0xFFFFEBEE) else MaterialTheme.colorScheme.primaryContainer,
                                textColor = if (task.priority == "High") Color(0xFFC62828) else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        if (task != upcomingTasks.take(3).last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }

        // Recent Observations
        if (recentObservations.isNotEmpty()) {
            item {
                SectionCard(
                    title = "Recent Sightings",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    trailingAction = {
                        TextButton(onClick = { navController.navigate(Screen.ObservationHistory.route) }) { Text("See all") }
                    }
                ) {
                    recentObservations.take(3).forEach { obs ->
                        ListItemRow(
                            title = obs.species,
                            subtitle = "${obs.location} · ${formatShortDate(obs.date)}",
                            leadingIcon = Icons.Filled.NaturePeople,
                            leadingIconTint = MaterialTheme.colorScheme.primary,
                            leadingIconBg = MaterialTheme.colorScheme.primaryContainer,
                            trailing = {
                                TagChip(label = "×${obs.count}")
                            }
                        )
                    }
                }
            }
        }

        // Navigation Shortcuts
        item {
            SectionCard(
                title = "Farm Sections",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                    val sections = listOf(
                        Triple("Poultry Groups", Icons.Filled.Groups, Screen.BirdGroups.route),
                        Triple("Egg Production", Icons.Filled.Egg, Screen.EggTracker.route),
                        Triple("Feed Storage", Icons.Filled.Inventory, Screen.FeedStorage.route),
                        Triple("Health Records", Icons.Filled.HealthAndSafety, Screen.Health.route),
                        Triple("Breeding", Icons.Filled.ChildFriendly, Screen.Breeding.route),
                        Triple("Reports", Icons.Filled.BarChart, Screen.Reports.route),
                        Triple("Calendar", Icons.Filled.CalendarMonth, Screen.Calendar.route),
                        Triple("Tasks", Icons.Filled.TaskAlt, Screen.Tasks.route),
                        Triple("Costs", Icons.Filled.AttachMoney, Screen.Costs.route)
                    )
                sections.forEach { (label, icon, route) ->
                    ListItemRow(
                        title = label,
                        leadingIcon = icon,
                        onClick = { navController.navigate(route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(label: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).width(72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, maxLines = 1)
        }
    }
}

private fun getCategoryIcon(category: String): ImageVector = when (category) {
    "Feeding" -> Icons.Filled.Restaurant
    "Health" -> Icons.Filled.HealthAndSafety
    "Cleaning" -> Icons.Filled.CleaningServices
    else -> Icons.Filled.TaskAlt
}
