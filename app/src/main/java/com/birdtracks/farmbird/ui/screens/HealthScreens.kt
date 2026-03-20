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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.data.db.entity.HealthRecordEntity
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun HealthScreen(navController: NavController, viewModel: AppViewModel) {
    val healthRecords by viewModel.healthRecords.collectAsState()
    val activeIssues = healthRecords.count { it.status == "Active" }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Monitoring", "Resolved")

    val filtered = healthRecords.filter { selectedFilter == "All" || it.status == selectedFilter }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(
                        listOf(
                            if (activeIssues > 0) Color(0xFFB71C1C) else Color(0xFF1B5E20),
                            if (activeIssues > 0) Color(0xFFC62828) else Color(0xFF2E7D32),
                            MaterialTheme.colorScheme.background
                        )
                    )
                ).padding(24.dp)
            ) {
                Column {
                    Text("Health Records", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        if (activeIssues > 0) "$activeIssues active issue(s) require attention" else "All birds are healthy",
                        style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // Vaccination reminders card
        item {
            SectionCard(title = "Vaccination Reminders", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                val vaccinations = listOf(
                    Triple("Newcastle Disease", "Every 2-3 months", Color(0xFF1565C0)),
                    Triple("Infectious Bronchitis", "Annually", Color(0xFF2E7D32)),
                    Triple("Marek's Disease", "Day-old chicks", Color(0xFF6A1B9A)),
                    Triple("Fowl Pox", "Annually (endemic areas)", Color(0xFFE65100))
                )
                vaccinations.forEach { (name, schedule, color) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
                        Text(name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(schedule, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Filter tabs
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }
        }

        // Add button
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${filtered.size} record${if (filtered.size != 1) "s" else ""}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = { navController.navigate(Screen.AddHealthRecord.route) }, shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp)); Text("Add Record")
                }
            }
        }

        if (filtered.isEmpty()) {
            item { EmptyState(icon = Icons.Filled.HealthAndSafety, title = "No health records", message = if (selectedFilter == "All") "All birds are healthy!" else "No $selectedFilter issues") }
        } else {
            items(filtered, key = { it.id }) { record ->
                HealthRecordCard(
                    record = record,
                    onStatusChange = { newStatus -> viewModel.updateHealthRecord(record.copy(status = newStatus)) },
                    onDelete = { viewModel.deleteHealthRecord(record) }
                )
            }
        }
    }
}

@Composable
private fun HealthRecordCard(record: HealthRecordEntity, onStatusChange: (String) -> Unit, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    val statusColor = when (record.status) {
        "Active" -> Color(0xFFC62828)
        "Monitoring" -> Color(0xFFF9A825)
        "Resolved" -> Color(0xFF2E7D32)
        else -> Color(0xFF455A64)
    }

    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.HealthAndSafety, null, tint = statusColor, modifier = Modifier.size(22.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(record.issue, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(record.groupName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(record.status)
                IconButton(onClick = { showDelete = true }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(8.dp))
            InfoRow("Treatment", record.treatment)
            Spacer(Modifier.height(4.dp))
            InfoRow("Date", formatDate(record.date))
            if (record.veterinarian.isNotEmpty()) { Spacer(Modifier.height(4.dp)); InfoRow("Vet", record.veterinarian) }
            if (record.notes.isNotEmpty()) { Spacer(Modifier.height(4.dp)); InfoRow("Notes", record.notes) }

            if (record.status == "Active" || record.status == "Monitoring") {
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (record.status == "Active") {
                        OutlinedButton(onClick = { onStatusChange("Monitoring") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Set Monitoring", style = MaterialTheme.typography.labelMedium) }
                    }
                    Button(onClick = { onStatusChange("Resolved") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) {
                        Text("Mark Resolved", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }

    if (showDelete) {
        AlertDialog(onDismissRequest = { showDelete = false },
            title = { Text("Delete Health Record?") }, text = { Text("Remove this health record for ${record.groupName}?") },
            confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AddHealthRecordScreen(navController: NavController, viewModel: AppViewModel) {
    val birdGroups by viewModel.birdGroups.collectAsState()

    var selectedGroupId by remember { mutableStateOf<Long?>(null) }
    var selectedGroupName by remember { mutableStateOf("") }
    var issue by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active") }
    var veterinarian by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var groupExpanded by remember { mutableStateOf(false) }
    var groupError by remember { mutableStateOf(false) }
    var issueError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val statuses = listOf("Active", "Monitoring", "Resolved")
    val commonIssues = listOf("Respiratory infection", "Diarrhea", "Eye infection", "Mites/Lice", "Bumblefoot", "Egg binding", "Marek's disease", "Coccidiosis", "Newcastle disease", "Injury", "Vaccination")

    if (showSuccess) { LaunchedEffect(Unit) { kotlinx.coroutines.delay(1200); navController.popBackStack() } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.HealthAndSafety, null, tint = Color(0xFFC62828), modifier = Modifier.size(28.dp))
                    Column {
                        Text("Health Record", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Log a health issue or treatment", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = !groupExpanded }) {
                OutlinedTextField(
                    value = selectedGroupName, onValueChange = {}, readOnly = true,
                    label = { Text("Bird Group *") }, leadingIcon = { Icon(Icons.Filled.Groups, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    isError = groupError, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                    birdGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Text(getBirdTypeEmoji(group.birdType)); Column { Text(group.name); Text("${group.count} birds", style = MaterialTheme.typography.bodySmall) } } },
                            onClick = { selectedGroupId = group.id; selectedGroupName = group.name; groupExpanded = false; groupError = false }
                        )
                    }
                }
            }
        }

        item {
            var issueExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = issueExpanded, onExpandedChange = { issueExpanded = !issueExpanded }) {
                OutlinedTextField(
                    value = issue, onValueChange = { issue = it; issueError = false },
                    label = { Text("Issue / Condition *") }, isError = issueError,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = issueExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Filled.Warning, null) }
                )
                ExposedDropdownMenu(expanded = issueExpanded, onDismissRequest = { issueExpanded = false }) {
                    commonIssues.forEach { ci ->
                        DropdownMenuItem(text = { Text(ci) }, onClick = { issue = ci; issueExpanded = false; issueError = false })
                    }
                }
            }
        }

        item { FormTextField(value = treatment, onValueChange = { treatment = it }, label = "Treatment / Action", placeholder = "Medication, dosage, quarantine measures...", singleLine = false, maxLines = 3) }

        item {
            Text("Status", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statuses.forEach { s ->
                    FilterChip(selected = status == s, onClick = { status = s }, label = { Text(s) })
                }
            }
        }

        item { FormTextField(value = veterinarian, onValueChange = { veterinarian = it }, label = "Veterinarian (optional)", placeholder = "Dr. Smith...") }
        item { FormTextField(value = notes, onValueChange = { notes = it }, label = "Notes", singleLine = false, maxLines = 3) }

        item {
            if (showSuccess) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2E7D32)); Text("Health record saved!", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        groupError = selectedGroupId == null; issueError = issue.isEmpty()
                        if (!groupError && !issueError) {
                            viewModel.addHealthRecord(selectedGroupId!!, selectedGroupName, issue, treatment, System.currentTimeMillis(), status, veterinarian, notes)
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp)); Text("Save Record", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}
