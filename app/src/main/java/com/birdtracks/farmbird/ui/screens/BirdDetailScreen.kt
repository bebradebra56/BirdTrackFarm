package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.InfoRow
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun BirdDetailScreen(navController: NavController, viewModel: AppViewModel, birdName: String) {
    val bird = viewModel.wildBirdsCatalog.firstOrNull { it.name == birdName }

    if (bird == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Bird not found")
        }
        return
    }

    val categoryColor = getCategoryColor(bird.category)
    val headerGradient = Brush.verticalGradient(
        listOf(categoryColor.copy(alpha = 0.8f), categoryColor.copy(alpha = 0.4f), MaterialTheme.colorScheme.background)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Header
        item {
            Box(
                modifier = Modifier.fillMaxWidth().height(240.dp).background(headerGradient),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(getCategoryEmoji(bird.category), style = MaterialTheme.typography.displayMedium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(bird.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            bird.scientificName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        // Category Badge
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = categoryColor),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        bird.category,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Description
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    Text(bird.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Migration Info
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.Flight, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Text("Migration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Season", bird.migrationSeason)
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Route", bird.migrationRoute)
                }
            }
        }

        // Details
        item {
            Card(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Habitat", bird.habitat)
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Diet", bird.diet)
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Lifespan", bird.lifespan)
                    Spacer(Modifier.height(6.dp))
                    InfoRow("Plumage", bird.color)
                }
            }
        }

        // Log Observation button
        item {
            Button(
                onClick = { navController.navigate(Screen.AddObservation.route + "?species=${bird.name}") },
                modifier = Modifier.padding(16.dp).fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Log Sighting of ${bird.name}", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}
