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
import com.birdtracks.farmbird.data.db.entity.BreedingRecordEntity
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.util.concurrent.TimeUnit

@Composable
fun BreedingScreen(navController: NavController, viewModel: AppViewModel) {
    val breedingRecords by viewModel.breedingRecords.collectAsState()
    val activeRecords = breedingRecords.filter { it.status == "Active" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFF4A148C), Color(0xFF6A1B9A), MaterialTheme.colorScheme.background))
                ).padding(24.dp)
            ) {
                Column {
                    Text("Breeding Program", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("${activeRecords.size} active incubation batch${if (activeRecords.size != 1) "es" else ""}", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Active incubations
        if (activeRecords.isNotEmpty()) {
            item {
                Text("Active Incubations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            items(activeRecords) { record ->
                ActiveIncubationCard(record = record, onClick = { navController.navigate(Screen.Incubator.createRoute(record.id)) })
            }
        }

        // Quick navigation
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { navController.navigate(Screen.Incubator.createRoute()) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("New Batch")
                }
                OutlinedButton(onClick = { navController.navigate(Screen.Chicks.route) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Filled.ChildFriendly, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("Chicks")
                }
            }
        }

        // Incubation guide
        item {
            SectionCard(title = "Incubation Guide", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                val guides = listOf(
                    listOf("Chicken", "21 days", "37.5°C / 99.5°F", "45-55%"),
                    listOf("Duck", "28 days", "37.5°C / 99.5°F", "55-65%"),
                    listOf("Goose", "29-31 days", "37.4°C / 99.3°F", "60-65%"),
                    listOf("Quail", "17-18 days", "37.5°C / 99.5°F", "45-50%")
                )
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Text("Bird", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                    Text("Days", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                    Text("Temp", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f))
                    Text("Humidity", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                }
                guides.forEach { (bird, days, temp, humidity) ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(getBirdTypeEmoji(bird) + " " + bird, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.2f))
                        Text(days, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.8f))
                        Text(temp, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
                        Text(humidity, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // History
        val completedRecords = breedingRecords.filter { it.status != "Active" }
        if (completedRecords.isNotEmpty()) {
            item {
                Text("History", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            items(completedRecords) { record ->
                BreedingHistoryCard(record = record, onDelete = { viewModel.deleteBreedingRecord(record) })
            }
        }
    }
}

@Composable
private fun ActiveIncubationCard(record: BreedingRecordEntity, onClick: () -> Unit) {
    val now = System.currentTimeMillis()
    val daysIn = TimeUnit.MILLISECONDS.toDays(now - record.startDate).toInt()
    val daysLeft = TimeUnit.MILLISECONDS.toDays(record.expectedHatchDate - now).toInt().coerceAtLeast(0)
    val totalDays = TimeUnit.MILLISECONDS.toDays(record.expectedHatchDate - record.startDate).toInt()
    val progress = (daysIn.toFloat() / totalDays).coerceIn(0f, 1f)

    Card(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF6A1B9A).copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text("🥚", style = MaterialTheme.typography.titleMedium)
                }
                Column(Modifier.weight(1f)) {
                    Text(record.groupName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("${record.incubatorEggs} eggs · ${record.temperature}°C", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$daysLeft", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                    Text("days left", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Day $daysIn of $totalDays", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Expected: ${formatDate(record.expectedHatchDate)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(6.dp))
            ProgressBar(progress, color = Color(0xFF6A1B9A))
        }
    }
}

@Composable
private fun BreedingHistoryCard(record: BreedingRecordEntity, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(Modifier.weight(1f)) {
                Text(record.groupName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Text("${record.incubatorEggs} eggs set · ${formatDate(record.startDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (record.actualHatched > 0) Text("${record.actualHatched} hatched", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
            }
            StatusBadge(record.status)
            IconButton(onClick = { showDelete = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
    if (showDelete) {
        AlertDialog(onDismissRequest = { showDelete = false },
            title = { Text("Delete Breeding Record?") }, text = { Text("Remove this breeding record?") },
            confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun IncubatorScreen(navController: NavController, viewModel: AppViewModel, recordId: Long) {
    val birdGroups by viewModel.birdGroups.collectAsState()
    val breedingRecords by viewModel.breedingRecords.collectAsState()
    val existingRecord = if (recordId > 0) breedingRecords.firstOrNull { it.id == recordId } else null

    var selectedGroupId by remember { mutableStateOf(existingRecord?.groupId) }
    var selectedGroupName by remember { mutableStateOf(existingRecord?.groupName ?: "") }
    var eggsStr by remember { mutableStateOf(existingRecord?.incubatorEggs?.toString() ?: "") }
    var tempStr by remember { mutableStateOf(existingRecord?.temperature?.toString() ?: "37.5") }
    var notes by remember { mutableStateOf(existingRecord?.notes ?: "") }
    var groupExpanded by remember { mutableStateOf(false) }
    var groupError by remember { mutableStateOf(false) }
    var eggsError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    if (existingRecord != null) {
        // View mode for existing record
        val now = System.currentTimeMillis()
        val daysIn = TimeUnit.MILLISECONDS.toDays(now - existingRecord.startDate).toInt()
        val daysLeft = TimeUnit.MILLISECONDS.toDays(existingRecord.expectedHatchDate - now).toInt().coerceAtLeast(0)
        val totalDays = TimeUnit.MILLISECONDS.toDays(existingRecord.expectedHatchDate - existingRecord.startDate).toInt()
        val progress = (daysIn.toFloat() / totalDays).coerceIn(0f, 1f)

        LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Text("Incubator Monitor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(existingRecord.groupName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("$daysLeft", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.ExtraBold, color = Color(0xFF6A1B9A))
                                Text("days left", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        ProgressBar(progress, color = Color(0xFF6A1B9A), height = 12.dp)
                        Spacer(Modifier.height(8.dp))
                        Text("Day $daysIn of $totalDays", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Eggs", "${existingRecord.incubatorEggs}", "in incubator", Icons.Filled.Egg, Color(0xFFFFF8E1), Color(0xFFE65100), Modifier.weight(1f))
                    StatCard("Temperature", "${existingRecord.temperature}°C", "setpoint", Icons.Filled.Thermostat, Color(0xFFFFEBEE), Color(0xFFC62828), Modifier.weight(1f))
                }
            }
            item { InfoRow("Started", formatDate(existingRecord.startDate)) }
            item { InfoRow("Expected Hatch", formatDate(existingRecord.expectedHatchDate)) }
            if (existingRecord.notes.isNotEmpty()) { item { InfoRow("Notes", existingRecord.notes) } }

            if (existingRecord.status == "Active") {
                item {
                    var hatchedStr by remember { mutableStateOf("") }
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Record Hatch Result", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(value = hatchedStr, onValueChange = { if (it.all { c -> c.isDigit() }) hatchedStr = it },
                                    label = { Text("Chicks hatched") },
                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                                Button(
                                    onClick = {
                                        val hatched = hatchedStr.toIntOrNull() ?: 0
                                        viewModel.updateBreedingRecord(existingRecord.copy(actualHatched = hatched, status = "Completed"))
                                        navController.popBackStack()
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Complete") }
                            }
                        }
                    }
                }
            }
        }
        return
    }

    if (showSuccess) { LaunchedEffect(Unit) { kotlinx.coroutines.delay(1200); navController.popBackStack() } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🥚", style = MaterialTheme.typography.headlineSmall)
                    Column {
                        Text("New Incubation Batch", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Set up a new incubation cycle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = !groupExpanded }) {
                OutlinedTextField(value = selectedGroupName, onValueChange = {}, readOnly = true,
                    label = { Text("Parent Group *") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    isError = groupError, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Filled.Groups, null) })
                ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                    birdGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Text(getBirdTypeEmoji(group.birdType)); Text(group.name) } },
                            onClick = { selectedGroupId = group.id; selectedGroupName = group.name; groupExpanded = false; groupError = false }
                        )
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = eggsStr, onValueChange = { if (it.all { c -> c.isDigit() }) eggsStr = it; eggsError = false },
                    label = { Text("Eggs Count *") }, isError = eggsError,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = tempStr, onValueChange = { if (it.matches(Regex("\\d*\\.?\\d*"))) tempStr = it },
                    label = { Text("Temperature °C") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            }
        }

        item { FormTextField(value = notes, onValueChange = { notes = it }, label = "Notes", singleLine = false, maxLines = 3) }

        item {
            if (showSuccess) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF6A1B9A)); Text("Incubation started!", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        groupError = selectedGroupId == null; eggsError = eggsStr.isEmpty()
                        if (!groupError && !eggsError) {
                            viewModel.addBreedingRecord(selectedGroupId!!, selectedGroupName, eggsStr.toIntOrNull() ?: 0, tempStr.toFloatOrNull() ?: 37.5f, System.currentTimeMillis(), notes)
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp)); Text("Start Incubation", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
fun ChicksScreen(navController: NavController, viewModel: AppViewModel) {
    val breedingRecords by viewModel.breedingRecords.collectAsState()
    val completedWithHatch = breedingRecords.filter { it.status == "Completed" && it.actualHatched > 0 }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            StatCard("Total Hatched", "${completedWithHatch.sumOf { it.actualHatched }}", "from ${completedWithHatch.size} batches",
                Icons.Filled.ChildFriendly, Color(0xFFF3E5F5), Color(0xFF6A1B9A), Modifier.fillMaxWidth())
        }

        if (completedWithHatch.isEmpty()) {
            item { EmptyState(icon = Icons.Filled.ChildFriendly, title = "No chick records", message = "Complete an incubation batch to see chick records here") }
        } else {
            items(completedWithHatch) { record ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("🐤", style = MaterialTheme.typography.headlineSmall)
                        Column(Modifier.weight(1f)) {
                            Text(record.groupName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                            Text("Hatched: ${formatDate(record.expectedHatchDate)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${record.incubatorEggs} eggs set · ${record.actualHatched} hatched", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${record.actualHatched}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A))
                            val rate = if (record.incubatorEggs > 0) (record.actualHatched * 100 / record.incubatorEggs) else 0
                            Text("$rate% rate", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
