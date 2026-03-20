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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.data.db.entity.BirdGroupEntity
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun BirdGroupsScreen(navController: NavController, viewModel: AppViewModel) {
    val birdGroups by viewModel.birdGroups.collectAsState()
    var deleteTarget by remember { mutableStateOf<BirdGroupEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (birdGroups.isEmpty()) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Filled.Groups,
                    title = "No Bird Groups",
                    message = "Add your first poultry group to start tracking",
                    actionLabel = "Add Group",
                    onAction = { navController.navigate(Screen.AddBirdGroup.createRoute()) }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${birdGroups.size} group${if (birdGroups.size != 1) "s" else ""} · ${birdGroups.sumOf { it.count }} total birds",
                            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(birdGroups, key = { it.id }) { group ->
                    BirdGroupCard(
                        group = group,
                        onDelete = { deleteTarget = group }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.AddBirdGroup.createRoute()) },
            modifier = Modifier.align(Alignment.End).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Filled.Add, "Add Group")
        }
    }

    deleteTarget?.let { group ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete ${group.name}?") },
            text = { Text("This will remove this group and all its data. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteBirdGroup(group); deleteTarget = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text("Cancel") } }
        )
    }
}

@Composable
fun BirdGroupCard(group: BirdGroupEntity, onDelete: () -> Unit) {
    val birdColor = when (group.birdType) {
        "Chicken" -> Color(0xFFFF8F00)
        "Duck" -> Color(0xFF0288D1)
        "Goose" -> Color(0xFF558B2F)
        "Quail" -> Color(0xFF795548)
        else -> Color(0xFF455A64)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape).background(birdColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(getBirdTypeEmoji(group.birdType), style = MaterialTheme.typography.titleLarge)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(group.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text("${group.birdType} · ${group.purpose}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GroupStat(icon = Icons.Filled.Groups, label = "Birds", value = "${group.count}")
                GroupStat(icon = Icons.Filled.AccessTime, label = "Age", value = "${group.ageWeeks}w")
                if (group.notes.isNotEmpty()) {
                    GroupStat(icon = Icons.Filled.Notes, label = "Notes", value = group.notes)
                }
            }
        }
    }
}

@Composable
private fun GroupStat(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
        Text("$label: $value", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AddBirdGroupScreen(navController: NavController, viewModel: AppViewModel) {
    val birdGroups by viewModel.birdGroups.collectAsState()
    var name by remember { mutableStateOf("") }
    var birdType by remember { mutableStateOf("Chicken") }
    var countStr by remember { mutableStateOf("") }
    var ageStr by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("Layers") }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var countError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val birdTypes = listOf("Chicken", "Duck", "Goose", "Quail", "Turkey", "Guinea Fowl")
    val purposes = listOf("Layers", "Broilers", "Meat", "Breeding", "Both")

    if (showSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1200)
            navController.popBackStack()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.Groups, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    Column {
                        Text("New Bird Group", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Create a poultry management group", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                }
            }
        }

        item {
            FormTextField(value = name, onValueChange = { name = it; nameError = false }, label = "Group Name *",
                placeholder = "e.g. Layer Hens, Broiler Batch 1", isError = nameError)
        }

        item {
            // Bird Type selector
            Text("Bird Type *", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                birdTypes.take(3).forEach { type ->
                    FilterChip(
                        selected = birdType == type,
                        onClick = { birdType = type },
                        label = { Text(getBirdTypeEmoji(type) + " " + type, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                birdTypes.drop(3).forEach { type ->
                    FilterChip(
                        selected = birdType == type,
                        onClick = { birdType = type },
                        label = { Text(getBirdTypeEmoji(type) + " " + type, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = countStr, onValueChange = { if (it.all { c -> c.isDigit() }) countStr = it; countError = false },
                    label = { Text("Bird Count *") }, isError = countError,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = ageStr, onValueChange = { if (it.all { c -> c.isDigit() }) ageStr = it },
                    label = { Text("Age (weeks)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Text("Purpose", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                purposes.forEach { p ->
                    FilterChip(selected = purpose == p, onClick = { purpose = p }, label = { Text(p, style = MaterialTheme.typography.labelSmall) })
                }
            }
        }

        item {
            FormTextField(value = notes, onValueChange = { notes = it }, label = "Notes",
                placeholder = "Breed, origin, special care notes...", singleLine = false, maxLines = 3)
        }

        item {
            if (showSuccess) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                        Text("Group created!", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        nameError = name.isEmpty(); countError = countStr.isEmpty()
                        if (!nameError && !countError) {
                            viewModel.addBirdGroup(name, birdType, countStr.toIntOrNull() ?: 1, ageStr.toIntOrNull() ?: 0, purpose, notes)
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Create Group", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}
