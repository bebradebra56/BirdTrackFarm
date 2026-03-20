package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.util.Calendar

@Composable
fun ReportsScreen(navController: NavController, viewModel: AppViewModel) {
    val eggRecords by viewModel.eggRecords.collectAsState()
    val feedRecords by viewModel.feedRecords.collectAsState()
    val costRecords by viewModel.costRecords.collectAsState()
    val birdGroups by viewModel.birdGroups.collectAsState()
    val observations by viewModel.observations.collectAsState()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsState()
    val weeklyEggs by viewModel.weeklyEggCount.collectAsState()

    val last30DaysEggs = remember(eggRecords) {
        val thirtyDaysAgo = System.currentTimeMillis() - 30L * 86_400_000L
        eggRecords.filter { it.date >= thirtyDaysAgo }.sumOf { it.count }
    }

    val last7DaysEggs = remember(eggRecords) {
        val sevenDaysAgo = System.currentTimeMillis() - 7L * 86_400_000L
        (0..6).map { daysAgo ->
            val dayStart = System.currentTimeMillis() - (6 - daysAgo) * 86_400_000L
            val dayEnd = dayStart + 86_400_000L
            eggRecords.filter { it.date in dayStart until dayEnd }.sumOf { it.count }
        }
    }

    val maxEggs = last7DaysEggs.maxOrNull()?.coerceAtLeast(1) ?: 1

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFF004D40), Color(0xFF00695C), MaterialTheme.colorScheme.background))
                ).padding(24.dp)
            ) {
                Column {
                    Text("Farm Analytics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Overview of your farm performance", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Key metrics
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Eggs (30d)", "$last30DaysEggs", "total", Icons.Filled.Egg, Color(0xFFFFF8E1), Color(0xFFE65100), Modifier.weight(1f))
                StatCard("Expenses", "$${"%.0f".format(monthlyExpenses)}", "this month", Icons.Filled.AttachMoney, Color(0xFFFFEBEE), Color(0xFFC62828), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Observations", "${observations.size}", "total", Icons.Filled.NaturePeople, Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f))
                StatCard("Poultry", "${birdGroups.sumOf { it.count }}", "total birds", Icons.Filled.Groups, Color(0xFFE3F2FD), Color(0xFF1565C0), Modifier.weight(1f))
            }
        }

        // Egg production chart
        item {
            Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Egg Production — Last 7 Days", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                        val w = size.width; val h = size.height
                        val barWidth = w / (last7DaysEggs.size * 2 - 1)
                        val primaryColor = Color(0xFF2D6A4F)

                        last7DaysEggs.forEachIndexed { i, count ->
                            val barH = (count.toFloat() / maxEggs) * (h - 20f)
                            val x = i * barWidth * 2
                            val y = h - barH
                            drawRect(primaryColor.copy(alpha = 0.8f), Offset(x, y), size = androidx.compose.ui.geometry.Size(barWidth, barH), style = androidx.compose.ui.graphics.drawscope.Fill)
                            if (count > 0) {
                                drawContext.canvas.nativeCanvas.drawText(
                                    "$count",
                                    x + barWidth / 2,
                                    y - 4f,
                                    android.graphics.Paint().apply {
                                        textAlign = android.graphics.Paint.Align.CENTER
                                        textSize = 24f
                                        color = android.graphics.Color.argb(220, 45, 106, 79)
                                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                                    }
                                )
                            }
                        }

                        // Baseline
                        drawLine(Color(0xFFDDDDDD), Offset(0f, h), Offset(w, h), strokeWidth = 1f)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("7d ago", "6d", "5d", "4d", "3d", "2d", "Today").forEach { label ->
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Cost breakdown chart
        val totalCosts = costRecords.sumOf { it.amount }
        if (totalCosts > 0) {
            item {
                Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Cost Breakdown", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        val categoryData = costRecords.groupBy { it.category }
                            .mapValues { it.value.sumOf { r -> r.amount } }
                            .entries.sortedByDescending { it.value }
                        val colors = listOf(Color(0xFF2E7D32), Color(0xFFC62828), Color(0xFF1565C0), Color(0xFFE65100), Color(0xFF6A1B9A))
                        categoryData.forEachIndexed { i, (cat, amount) ->
                            val pct = (amount / totalCosts * 100).toInt()
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(colors[i % colors.size]))
                                Text(cat, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text("$pct%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("$${"%.2f".format(amount)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                            }
                            ProgressBar((amount / totalCosts).toFloat(), color = colors[i % colors.size], height = 6.dp)
                            Spacer(Modifier.height(2.dp))
                        }
                    }
                }
            }
        }

        // Bird group summary
        item {
            SectionCard(title = "Flock Summary", modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                birdGroups.forEach { group ->
                    val groupEggs = eggRecords.filter { it.groupId == group.id }.sumOf { it.count }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(getBirdTypeEmoji(group.birdType), style = MaterialTheme.typography.titleMedium)
                        Column(Modifier.weight(1f)) {
                            Text(group.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("${group.birdType} · ${group.count} birds · ${group.purpose}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (groupEggs > 0) TagChip("$groupEggs eggs")
                    }
                    if (group != birdGroups.last()) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(vertical = 2.dp))
                }
                if (birdGroups.isEmpty()) Text("No bird groups yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Navigation to other screens
        item {
            SectionCard(title = "More Reports", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                ListItemRow("Activity History", "All farm activities timeline", Icons.Filled.History, onClick = { navController.navigate("activity_history") })
                ListItemRow("Tasks Overview", "Track pending & completed tasks", Icons.Filled.TaskAlt, onClick = { navController.navigate("tasks") })
                ListItemRow("Costs Analysis", "Detailed expense breakdown", Icons.Filled.AttachMoney, onClick = { navController.navigate("costs") })
                ListItemRow("Equipment Status", "Track equipment health", Icons.Filled.Build, onClick = { navController.navigate("equipment") })
            }
        }
    }
}

@Composable
fun ActivityHistoryScreen(navController: NavController, viewModel: AppViewModel) {
    val observations by viewModel.observations.collectAsState()
    val eggRecords by viewModel.eggRecords.collectAsState()
    val feedRecords by viewModel.feedRecords.collectAsState()
    val healthRecords by viewModel.healthRecords.collectAsState()
    val breedingRecords by viewModel.breedingRecords.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    data class ActivityItem(val timestamp: Long, val emoji: String, val title: String, val subtitle: String, val color: Color)

    val activities = remember(observations, eggRecords, feedRecords, healthRecords, breedingRecords, tasks) {
        buildList {
            observations.forEach { add(ActivityItem(it.createdAt, "🔭", "Observed: ${it.species}", "${it.count} bird(s) at ${it.location}", Color(0xFF1565C0))) }
            eggRecords.forEach { add(ActivityItem(it.createdAt, "🥚", "Egg Collection: ${it.count} eggs", it.groupName, Color(0xFFE65100))) }
            feedRecords.forEach { add(ActivityItem(it.createdAt, "🌾", "Feeding: ${it.feedType}", "${it.amount}${it.unit}", Color(0xFF2E7D32))) }
            healthRecords.forEach { add(ActivityItem(it.createdAt, "🏥", "Health: ${it.issue}", it.groupName, Color(0xFFC62828))) }
            breedingRecords.forEach { add(ActivityItem(it.createdAt, "🥚", "Incubation started: ${it.incubatorEggs} eggs", it.groupName, Color(0xFF6A1B9A))) }
            tasks.filter { it.isCompleted }.forEach { add(ActivityItem(it.createdAt, "✅", "Task completed: ${it.title}", it.category, Color(0xFF2E7D32))) }
        }.sortedByDescending { it.timestamp }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item { Text("${activities.size} activities", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp)) }

        if (activities.isEmpty()) {
            item { EmptyState(icon = Icons.Filled.History, title = "No activities yet", message = "Your farm activity history will appear here") }
        } else {
            items(activities) { activity ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(activity.color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                            Text(activity.emoji, style = MaterialTheme.typography.titleSmall)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(activity.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            if (activity.subtitle.isNotEmpty()) Text(activity.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(formatShortDate(activity.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
