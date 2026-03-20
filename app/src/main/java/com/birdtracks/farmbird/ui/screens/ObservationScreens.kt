@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.util.Calendar

@Composable
fun AddObservationScreen(navController: NavController, viewModel: AppViewModel, preselectedSpecies: String = "") {
    val birds = viewModel.wildBirdsCatalog
    var selectedSpecies by remember { mutableStateOf(preselectedSpecies) }
    var location by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var countStr by remember { mutableStateOf("1") }
    var speciesExpanded by remember { mutableStateOf(false) }
    var speciesQuery by remember { mutableStateOf(preselectedSpecies) }

    var speciesError by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500)
            navController.popBackStack()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.NaturePeople, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                    Column {
                        Text("Log Bird Sighting", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Record a wild bird observation", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                    }
                }
            }
        }

        item {
            // Species dropdown
            ExposedDropdownMenuBox(expanded = speciesExpanded, onExpandedChange = { speciesExpanded = !speciesExpanded }) {
                OutlinedTextField(
                    value = speciesQuery,
                    onValueChange = { speciesQuery = it; selectedSpecies = ""; speciesExpanded = true },
                    label = { Text("Bird Species *") },
                    leadingIcon = { Text(if (selectedSpecies.isNotEmpty()) getCategoryEmoji(birds.firstOrNull { it.name == selectedSpecies }?.category ?: "") else "🐦") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                    isError = speciesError,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = speciesExpanded, onDismissRequest = { speciesExpanded = false }) {
                    val filtered = birds.filter { it.name.contains(speciesQuery, ignoreCase = true) }
                    if (filtered.isEmpty()) {
                        DropdownMenuItem(text = { Text("No match – type custom species") }, onClick = {
                            selectedSpecies = speciesQuery; speciesExpanded = false
                        })
                    }
                    filtered.forEach { bird ->
                        DropdownMenuItem(
                            text = {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(getCategoryEmoji(bird.category))
                                    Column {
                                        Text(bird.name, style = MaterialTheme.typography.bodyMedium)
                                        Text(bird.category, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            },
                            onClick = {
                                selectedSpecies = bird.name; speciesQuery = bird.name
                                speciesExpanded = false; speciesError = false
                            }
                        )
                    }
                }
            }
        }

        item {
            FormTextField(
                value = location,
                onValueChange = { location = it; locationError = false },
                label = "Location *",
                placeholder = "e.g. North Field, River Bank...",
                leadingIcon = Icons.Filled.LocationOn,
                isError = locationError
            )
        }

        item {
            OutlinedTextField(
                value = countStr,
                onValueChange = { if (it.all { c -> c.isDigit() }) countStr = it },
                label = { Text("Count") },
                leadingIcon = { Icon(Icons.Filled.Tag, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            FormTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes",
                placeholder = "Behavior, weather conditions, plumage details...",
                singleLine = false,
                maxLines = 4
            )
        }

        item {
            if (showSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                        Text("Observation saved!", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        speciesError = selectedSpecies.isEmpty() && speciesQuery.isEmpty()
                        locationError = location.isEmpty()
                        if (!speciesError && !locationError) {
                            viewModel.addObservation(
                                species = selectedSpecies.ifEmpty { speciesQuery },
                                location = location,
                                date = System.currentTimeMillis(),
                                notes = notes,
                                count = countStr.toIntOrNull() ?: 1
                            )
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Observation", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
fun ObservationHistoryScreen(navController: NavController, viewModel: AppViewModel) {
    val observations by viewModel.observations.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filtered = observations.filter {
        searchQuery.isEmpty() || it.species.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search observations...") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Filled.Clear, null) } },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        if (filtered.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Filled.NaturePeople,
                    title = "No observations yet",
                    message = if (searchQuery.isEmpty()) "Start logging your bird sightings!" else "No results for \"$searchQuery\"",
                    actionLabel = if (searchQuery.isEmpty()) "Log First Sighting" else null,
                    onAction = if (searchQuery.isEmpty()) ({ navController.navigate("add_observation") }) else null
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("${filtered.size} observation${if (filtered.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                items(filtered, key = { it.id }) { obs ->
                    var showDelete by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                val birdData = viewModel.wildBirdsCatalog.firstOrNull { it.name == obs.species }
                                Text(getCategoryEmoji(birdData?.category ?: ""), style = MaterialTheme.typography.titleMedium)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(obs.species, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text(obs.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(formatDate(obs.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (obs.notes.isNotEmpty()) {
                                    Text(obs.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                TagChip(label = "×${obs.count}")
                                IconButton(onClick = { showDelete = true }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                    if (showDelete) {
                        AlertDialog(
                            onDismissRequest = { showDelete = false },
                            title = { Text("Delete Observation?") },
                            text = { Text("Remove this sighting of ${obs.species}?") },
                            confirmButton = {
                                TextButton(onClick = { viewModel.deleteObservation(obs); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
                            },
                            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
                        )
                    }
                }
            }
        }
    }
}
