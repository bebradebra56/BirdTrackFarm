@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.birdtracks.farmbird.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.data.db.entity.EggRecordEntity
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.util.Calendar

@Composable
fun EggTrackerScreen(navController: NavController, viewModel: AppViewModel) {
    val eggRecords by viewModel.eggRecords.collectAsState()
    val todayEggs by viewModel.todayEggCount.collectAsState()
    val weeklyEggs by viewModel.weeklyEggCount.collectAsState()
    val birdGroups by viewModel.birdGroups.collectAsState()

    // Calculate last 7 days data for chart
    val last7Days = remember(eggRecords) {
        val cal = Calendar.getInstance()
        (6 downTo 0).map { daysAgo ->
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val dayStart = cal.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
            val dayEnd = dayStart + 86_400_000L
            val count = eggRecords.filter { it.date in dayStart until dayEnd }.sumOf { it.count }
            Pair(formatShortDate(dayStart), count)
        }
    }
    val maxEggs = last7Days.maxOfOrNull { it.second } ?: 1

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFF57F17), MaterialTheme.colorScheme.background))
                ).padding(24.dp)
            ) {
                Column {
                    Text("Egg Production", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Track daily egg collection", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Stats
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Today", "$todayEggs", "eggs collected", Icons.Filled.Egg, Color(0xFFFFF8E1), Color(0xFFE65100), Modifier.weight(1f))
                StatCard("This Week", "$weeklyEggs", "total eggs", Icons.Filled.DateRange, Color(0xFFFFF3E0), Color(0xFFBF360C), Modifier.weight(1f))
            }
        }

        // 7-day bar chart
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Last 7 Days", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        last7Days.forEach { (day, count) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (count > 0) {
                                    Text("$count", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .fillMaxHeight((count.toFloat() / maxEggs).coerceAtLeast(0.04f))
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(day.take(3), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // By group breakdown
        if (birdGroups.isNotEmpty()) {
            item {
                SectionCard(title = "By Group", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    birdGroups.filter { it.purpose == "Layers" || it.purpose == "Both" }.forEach { group ->
                        val groupEggs = eggRecords.filter { it.groupId == group.id }.sumOf { it.count }
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(getBirdTypeEmoji(group.birdType), style = MaterialTheme.typography.titleMedium)
                            Column(Modifier.weight(1f)) {
                                Text(group.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text("${group.count} birds", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text("$groupEggs total", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Recent records header
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Records", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Button(
                    onClick = { navController.navigate("add_egg_record") },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Log Eggs")
                }
            }
        }

        if (eggRecords.isEmpty()) {
            item {
                EmptyState(icon = Icons.Filled.Egg, title = "No egg records", message = "Start logging your daily egg collection")
            }
        } else {
            items(eggRecords.take(20), key = { it.id }) { record ->
                EggRecordItem(record = record, onDelete = { viewModel.deleteEggRecord(record) })
            }
        }
    }
}

@Composable
private fun EggRecordItem(record: EggRecordEntity, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFF8E1)), contentAlignment = Alignment.Center) {
                Text("🥚", style = MaterialTheme.typography.titleMedium)
            }
            Column(Modifier.weight(1f)) {
                Text(record.groupName.ifEmpty { "Unknown Group" }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text(formatDate(record.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (record.notes.isNotEmpty()) Text(record.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${record.count}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("eggs", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { showDelete = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
    if (showDelete) {
        AlertDialog(onDismissRequest = { showDelete = false },
            title = { Text("Delete Record?") },
            text = { Text("Remove this egg record?") },
            confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AddEggRecordScreen(navController: NavController, viewModel: AppViewModel) {
    val birdGroups by viewModel.birdGroups.collectAsState()
    val layingGroups = birdGroups.filter { it.purpose == "Layers" || it.purpose == "Both" }

    var selectedGroupId by remember { mutableStateOf<Long?>(null) }
    var selectedGroupName by remember { mutableStateOf("") }
    var countStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var groupExpanded by remember { mutableStateOf(false) }
    var countError by remember { mutableStateOf(false) }
    var groupError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) { LaunchedEffect(Unit) { kotlinx.coroutines.delay(1200); navController.popBackStack() } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🥚", style = MaterialTheme.typography.headlineSmall)
                    Column {
                        Text("Log Egg Collection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Record eggs for a bird group", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = !groupExpanded }) {
                OutlinedTextField(
                    value = selectedGroupName, onValueChange = {},
                    label = { Text("Bird Group *") }, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    isError = groupError, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Filled.Groups, null) }
                )
                ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                    if (layingGroups.isEmpty()) {
                        DropdownMenuItem(text = { Text("No laying groups found") }, onClick = { groupExpanded = false })
                    }
                    layingGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Text(getBirdTypeEmoji(group.birdType)); Column { Text(group.name); Text("${group.count} birds", style = MaterialTheme.typography.bodySmall) } } },
                            onClick = { selectedGroupId = group.id; selectedGroupName = group.name; groupExpanded = false; groupError = false }
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = countStr, onValueChange = { if (it.all { c -> c.isDigit() }) countStr = it; countError = false },
                label = { Text("Egg Count *") }, leadingIcon = { Text("🥚") }, isError = countError,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            FormTextField(value = notes, onValueChange = { notes = it }, label = "Notes", placeholder = "Quality, broken eggs, unusual finds...", singleLine = false, maxLines = 3)
        }

        item {
            if (showSuccess) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🥚"); Text("${countStr} eggs logged!", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        groupError = selectedGroupId == null; countError = countStr.isEmpty()
                        if (!groupError && !countError) {
                            viewModel.addEggRecord(selectedGroupId!!, selectedGroupName, countStr.toIntOrNull() ?: 0, System.currentTimeMillis(), notes)
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Record", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}
