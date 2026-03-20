package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun PoultryScreen(navController: NavController, viewModel: AppViewModel) {
    val birdGroups by viewModel.birdGroups.collectAsState()
    val totalPoultry by viewModel.totalPoultryCount.collectAsState()
    val todayEggs by viewModel.todayEggCount.collectAsState()
    val activeHealth by viewModel.activeHealthIssues.collectAsState()
    val breedingRecords by viewModel.breedingRecords.collectAsState()
    val activeBreeding = breedingRecords.count { it.status == "Active" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header gradient
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFF0D47A1), Color(0xFF1565C0), MaterialTheme.colorScheme.background))
                ).padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text("Poultry Management", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("$totalPoultry birds in ${birdGroups.size} groups", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Stats
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Total Birds", "$totalPoultry", "in ${birdGroups.size} groups", Icons.Filled.Groups, Color(0xFFE3F2FD), Color(0xFF0D47A1), Modifier.weight(1f))
                StatCard("Eggs Today", "$todayEggs", "collected", Icons.Filled.Egg, Color(0xFFFFF8E1), Color(0xFFE65100), Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("Health", if (activeHealth == 0) "All Good" else "$activeHealth Issues", "", Icons.Filled.HealthAndSafety,
                    if (activeHealth == 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    if (activeHealth == 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                    Modifier.weight(1f))
                StatCard("Breeding", "$activeBreeding active", "programs", Icons.Filled.ChildFriendly, Color(0xFFF3E5F5), Color(0xFF6A1B9A), Modifier.weight(1f))
            }
        }

        // Bird type breakdown
        item {
            SectionCard(
                title = "Bird Types",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                val typeGroups = birdGroups.groupBy { it.birdType }
                val types = listOf("Chicken", "Duck", "Goose", "Quail")
                types.forEach { type ->
                    val groups = typeGroups[type]
                    val count = groups?.sumOf { it.count } ?: 0
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(getBirdTypeEmoji(type), style = MaterialTheme.typography.titleMedium)
                        Column(Modifier.weight(1f)) {
                            Text(type + "s", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("${groups?.size ?: 0} group(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text("$count birds", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                    if (type != types.last()) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }

        // Management sections
        item {
            Text("Manage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
        }

        items(getPoultryMenuItems(navController)) { item ->
            PoultryMenuCard(item = item, onClick = { navController.navigate(item.route) })
        }
    }
}

data class PoultryMenuItem(val title: String, val subtitle: String, val icon: ImageVector, val route: String, val color: Color)

fun getPoultryMenuItems(navController: NavController? = null): List<PoultryMenuItem> = listOf(
    PoultryMenuItem("Bird Groups", "Manage your flocks", Icons.Filled.Groups, Screen.BirdGroups.route, Color(0xFF1565C0)),
    PoultryMenuItem("Egg Tracker", "Daily egg production", Icons.Filled.Egg, Screen.EggTracker.route, Color(0xFFF57F17)),
    PoultryMenuItem("Feeding", "Feed schedules & records", Icons.Filled.Restaurant, Screen.Feeding.route, Color(0xFF2E7D32)),
    PoultryMenuItem("Feed Storage", "Stock management", Icons.Filled.Inventory, Screen.FeedStorage.route, Color(0xFF00838F)),
    PoultryMenuItem("Health", "Health & vaccinations", Icons.Filled.HealthAndSafety, Screen.Health.route, Color(0xFFC62828)),
    PoultryMenuItem("Breeding", "Incubation & chicks", Icons.Filled.ChildFriendly, Screen.Breeding.route, Color(0xFF6A1B9A))
)

@Composable
private fun PoultryMenuCard(item: PoultryMenuItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(
                modifier = Modifier.size(48.dp).background(item.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, tint = item.color, modifier = Modifier.size(26.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(item.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun getBirdTypeEmoji(type: String): String = when (type) {
    "Chicken" -> "🐓"
    "Duck" -> "🦆"
    "Goose" -> "🦢"
    "Quail" -> "🐤"
    else -> "🐦"
}
