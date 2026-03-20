@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.data.db.entity.CostRecordEntity
import com.birdtracks.farmbird.data.db.entity.EquipmentEntity
import com.birdtracks.farmbird.data.db.entity.TaskEntity
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.viewmodel.AppViewModel

// ─────────────────────── TASKS ───────────────────────

@Composable
fun TasksScreen(navController: NavController, viewModel: AppViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    val pendingTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }

    val filtered = when (selectedFilter) {
        "Pending" -> pendingTasks
        "Completed" -> completedTasks
        else -> tasks
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Stats row
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Pending", "${pendingTasks.size}", "tasks", Icons.Filled.PendingActions, MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer, Modifier.weight(1f))
            StatCard("Done", "${completedTasks.size}", "completed", Icons.Filled.CheckCircle, Color(0xFFE8F5E9), Color(0xFF2E7D32), Modifier.weight(1f))
        }

        // Filter
        Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Pending", "Completed").forEach { f ->
                FilterChip(selected = selectedFilter == f, onClick = { selectedFilter = f }, label = { Text(f) })
            }
        }

        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered, key = { it.id }) { task ->
                TaskCard(task = task,
                    onToggle = { viewModel.toggleTask(task) },
                    onDelete = { viewModel.deleteTask(task) })
            }
            if (filtered.isEmpty()) {
                item { EmptyState(icon = Icons.Filled.TaskAlt, title = if (selectedFilter == "Completed") "No completed tasks" else "No tasks!", message = if (selectedFilter == "Pending") "All tasks done! Great job!" else "Add a task to get started.") }
            }
        }

        FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.align(Alignment.End).padding(16.dp)) {
            Icon(Icons.Filled.Add, "Add Task")
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, desc, category, priority ->
                viewModel.addTask(title, desc, System.currentTimeMillis() + 86_400_000L, category, priority)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TaskCard(task: TaskEntity, onToggle: () -> Unit, onDelete: () -> Unit) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFC62828)
        "Normal" -> MaterialTheme.colorScheme.primary
        else -> Color(0xFF555555)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(if (task.isCompleted) 0.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle() })
            Column(Modifier.weight(1f)) {
                Text(
                    task.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                if (task.description.isNotEmpty()) Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(formatDate(task.dueDate), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TagChip(task.category, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f), MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            if (task.priority == "High") Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(priorityColor))
            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun AddTaskDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Other") }
    var priority by remember { mutableStateOf("Normal") }
    val categories = listOf("Feeding", "Health", "Cleaning", "Breeding", "Other")
    val priorities = listOf("High", "Normal", "Low")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Text("Category:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
                Text("Priority:", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    priorities.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { if (title.isNotEmpty()) onAdd(title, desc, category, priority) }, enabled = title.isNotEmpty()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// ─────────────────────── EQUIPMENT ───────────────────────

@Composable
fun EquipmentScreen(navController: NavController, viewModel: AppViewModel) {
    val equipment by viewModel.equipment.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total", "${equipment.size}", "items", Icons.Filled.Build, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, Modifier.weight(1f))
                    StatCard("Issues", "${equipment.count { it.status != "Working" }}", "need attention", Icons.Filled.Warning, Color(0xFFFFEBEE), Color(0xFFC62828), Modifier.weight(1f))
                }
            }
            items(equipment, key = { it.id }) { item ->
                EquipmentCard(item = item,
                    onStatusChange = { viewModel.updateEquipment(item.copy(status = it)) },
                    onDelete = { viewModel.deleteEquipment(item) })
            }
            if (equipment.isEmpty()) {
                item { EmptyState(icon = Icons.Filled.Build, title = "No equipment", message = "Add your farm equipment to track status") }
            }
        }
        FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.align(Alignment.End).padding(16.dp)) {
            Icon(Icons.Filled.Add, "Add Equipment")
        }
    }

    if (showAddDialog) {
        AddEquipmentDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, type, status, notes -> viewModel.addEquipment(name, type, status, notes); showAddDialog = false }
        )
    }
}

@Composable
private fun EquipmentCard(item: EquipmentEntity, onStatusChange: (String) -> Unit, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Build, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(item.type, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (item.notes.isNotEmpty()) Text(item.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                StatusBadge(item.status)
                ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                    listOf("Working", "Maintenance", "Broken").forEach { s ->
                        DropdownMenuItem(text = { Text(s) }, onClick = { onStatusChange(s); statusExpanded = false })
                    }
                }
            }
            IconButton(onClick = { showDelete = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
    if (showDelete) AlertDialog(onDismissRequest = { showDelete = false }, title = { Text("Delete ${item.name}?") }, text = { Text("Remove this equipment?") },
        confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } })
}

@Composable
private fun AddEquipmentDialog(onDismiss: () -> Unit, onAdd: (String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Working") }
    var notes by remember { mutableStateOf("") }
    val types = listOf("Incubator", "Feeder", "Drinker", "Heating", "Lighting", "Coop Equipment", "Other")
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Add Equipment") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Equipment Name *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = it }) {
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp))
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        types.forEach { t -> DropdownMenuItem(text = { Text(t) }, onClick = { type = t; typeExpanded = false }) }
                    }
                }
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = { Button(onClick = { if (name.isNotEmpty()) onAdd(name, type, status, notes) }, enabled = name.isNotEmpty()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// ─────────────────────── COSTS ───────────────────────

@Composable
fun CostsScreen(navController: NavController, viewModel: AppViewModel) {
    val costRecords by viewModel.costRecords.collectAsState()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    val byCategory = costRecords.groupBy { it.category }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 80.dp)) {
            item {
                Card(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Monthly Expenses", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text("$${"%.2f".format(monthlyExpenses)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("This month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                }
            }

            // Category breakdown
            item {
                SectionCard(title = "By Category", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    val categoryColors = mapOf("Feed" to Color(0xFF2E7D32), "Medicine" to Color(0xFFC62828), "Equipment" to Color(0xFF1565C0), "Other" to Color(0xFF455A64))
                    val totalExpenses = costRecords.sumOf { it.amount }
                    byCategory.entries.sortedByDescending { it.value.sumOf { r -> r.amount } }.forEach { (category, records) ->
                        val categoryTotal = records.sumOf { it.amount }
                        val pct = if (totalExpenses > 0) (categoryTotal / totalExpenses * 100).toInt() else 0
                        val color = categoryColors[category] ?: Color(0xFF455A64)
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
                            Text(category, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text("$pct%", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${"%.2f".format(categoryTotal)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = color)
                        }
                        ProgressBar((categoryTotal / totalExpenses.coerceAtLeast(0.01)).toFloat(), color = color, height = 6.dp)
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            // Recent records
            item { SectionHeader("Recent Expenses", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) }

            items(costRecords.take(20), key = { it.id }) { record ->
                CostRecordItem(record = record, onDelete = { viewModel.deleteCostRecord(record) })
            }
            if (costRecords.isEmpty()) {
                item { EmptyState(icon = Icons.Filled.AttachMoney, title = "No expenses recorded", message = "Track your farm costs to improve efficiency") }
            }
        }

        FloatingActionButton(onClick = { showAddDialog = true }, modifier = Modifier.align(Alignment.End).padding(16.dp)) {
            Icon(Icons.Filled.Add, "Add Cost")
        }
    }

    if (showAddDialog) {
        AddCostDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { category, amount, desc -> viewModel.addCostRecord(category, amount, desc, System.currentTimeMillis()); showAddDialog = false }
        )
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = modifier)
}

@Composable
private fun CostRecordItem(record: CostRecordEntity, onDelete: () -> Unit) {
    val categoryColor = mapOf("Feed" to Color(0xFF2E7D32), "Medicine" to Color(0xFFC62828), "Equipment" to Color(0xFF1565C0))[record.category] ?: Color(0xFF455A64)
    var showDelete by remember { mutableStateOf(false) }
    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp).fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(categoryColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.AttachMoney, null, tint = categoryColor, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(record.description, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TagChip(record.category, categoryColor.copy(alpha = 0.12f), categoryColor)
                    Text(formatDate(record.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text("$${"%.2f".format(record.amount)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = categoryColor)
            IconButton(onClick = { showDelete = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
    if (showDelete) AlertDialog(onDismissRequest = { showDelete = false }, title = { Text("Delete expense?") }, text = { Text("Remove this cost record?") },
        confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } })
}

@Composable
private fun AddCostDialog(onDismiss: () -> Unit, onAdd: (String, Double, String) -> Unit) {
    var category by remember { mutableStateOf("Feed") }
    var amountStr by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val categories = listOf("Feed", "Medicine", "Equipment", "Utilities", "Labor", "Other")
    AlertDialog(
        onDismissRequest = onDismiss, title = { Text("Add Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Category:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.take(3).forEach { c -> FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c, style = MaterialTheme.typography.labelSmall) }) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.drop(3).forEach { c -> FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c, style = MaterialTheme.typography.labelSmall) }) }
                }
                OutlinedTextField(value = amountStr, onValueChange = { if (it.matches(Regex("\\d*\\.?\\d*"))) amountStr = it }, label = { Text("Amount ($) *") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = { Icon(Icons.Filled.AttachMoney, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = { Button(onClick = { if (amountStr.isNotEmpty() && desc.isNotEmpty()) onAdd(category, amountStr.toDoubleOrNull() ?: 0.0, desc) }, enabled = amountStr.isNotEmpty() && desc.isNotEmpty()) { Text("Add") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
