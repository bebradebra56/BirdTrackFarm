@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.TagChip
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel
import com.birdtracks.farmbird.viewmodel.WildBirdSpecies

@Composable
fun WildBirdsScreen(navController: NavController, viewModel: AppViewModel) {
    val birds = viewModel.wildBirdsCatalog
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Migratory", "Waterfowl", "Songbird", "Wading Bird", "Raptor")

    val filteredBirds = birds.filter { bird ->
        val matchesSearch = searchQuery.isEmpty() ||
                bird.name.contains(searchQuery, ignoreCase = true) ||
                bird.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || bird.category == selectedCategory
        matchesSearch && matchesCategory
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search birds...") },
                    leadingIcon = { Icon(Icons.Filled.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Filled.Clear, null) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }
        }

        // Category filter
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) },
                        leadingIcon = if (selectedCategory == cat) ({ Icon(Icons.Filled.Check, null, Modifier.size(16.dp)) }) else null
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // Quick actions
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.AddObservation.route) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Log Sighting")
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.MigrationMap.route) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Map, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Migration Map")
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        item {
            Padding16 {
                Text(
                    "${filteredBirds.size} species",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(filteredBirds) { bird ->
            BirdSpeciesCard(bird = bird, onClick = {
                navController.navigate(Screen.BirdDetail.createRoute(bird.name))
            })
        }
    }
}

@Composable
fun BirdSpeciesCard(bird: WildBirdSpecies, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(bird.category).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryEmoji(bird.category),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(bird.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    TagChip(
                        label = bird.category,
                        color = getCategoryColor(bird.category).copy(alpha = 0.15f),
                        textColor = getCategoryColor(bird.category)
                    )
                }
                Text(
                    bird.scientificName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Flight, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                    Text(bird.migrationSeason, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun Padding16(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) { content() }
}

fun getCategoryColor(category: String): Color = when (category) {
    "Migratory" -> Color(0xFF1565C0)
    "Waterfowl" -> Color(0xFF00838F)
    "Songbird" -> Color(0xFF558B2F)
    "Wading Bird" -> Color(0xFF6A1B9A)
    "Raptor" -> Color(0xFFC62828)
    else -> Color(0xFF455A64)
}

fun getCategoryEmoji(category: String): String = when (category) {
    "Migratory" -> "🐦"
    "Waterfowl" -> "🦆"
    "Songbird" -> "🐤"
    "Wading Bird" -> "🦢"
    "Raptor" -> "🦅"
    else -> "🐧"
}
