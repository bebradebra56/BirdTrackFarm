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
import com.birdtracks.farmbird.ui.components.TagChip
import com.birdtracks.farmbird.viewmodel.AppViewModel

data class MigrationRoute(
    val birdName: String,
    val category: String,
    val from: String,
    val to: String,
    val color: Color,
    val points: List<Pair<Float, Float>>  // normalized 0..1 coordinates
)

@Composable
fun MigrationMapScreen(navController: NavController, viewModel: AppViewModel) {
    var selectedBird by remember { mutableStateOf<String?>(null) }

    val routes = listOf(
        MigrationRoute("Barn Swallow", "Migratory", "Sub-Saharan Africa", "Europe", Color(0xFF1565C0),
            listOf(0.5f to 0.75f, 0.5f to 0.55f, 0.48f to 0.38f, 0.46f to 0.28f)),
        MigrationRoute("White Stork", "Migratory", "Africa", "Europe", Color(0xFF43A047),
            listOf(0.52f to 0.72f, 0.53f to 0.55f, 0.52f to 0.40f, 0.51f to 0.27f)),
        MigrationRoute("Common Crane", "Migratory", "India/Africa", "N. Europe", Color(0xFF7B1FA2),
            listOf(0.65f to 0.55f, 0.6f to 0.45f, 0.55f to 0.35f, 0.52f to 0.22f)),
        MigrationRoute("Canada Goose", "Waterfowl", "Canada", "Southern US", Color(0xFFE65100),
            listOf(0.2f to 0.2f, 0.22f to 0.3f, 0.23f to 0.38f, 0.24f to 0.45f)),
        MigrationRoute("Common Swift", "Migratory", "Central Africa", "Europe", Color(0xFFF9A825),
            listOf(0.51f to 0.7f, 0.50f to 0.57f, 0.49f to 0.43f, 0.47f to 0.3f))
    )

    val displayedRoutes = if (selectedBird != null) routes.filter { it.birdName == selectedBird } else routes

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            // Map Canvas
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFF0D47A1))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Simplified continent outlines (approximate shapes)
                        val continentPaint = Paint().apply {
                            color = Color(0xFF2E7D32)
                            style = PaintingStyle.Fill
                        }

                        // North America outline (simplified)
                        drawPath(
                            path = Path().apply {
                                moveTo(w * 0.05f, h * 0.1f); lineTo(w * 0.32f, h * 0.08f)
                                lineTo(w * 0.35f, h * 0.25f); lineTo(w * 0.28f, h * 0.35f)
                                lineTo(w * 0.25f, h * 0.5f); lineTo(w * 0.27f, h * 0.6f)
                                lineTo(w * 0.18f, h * 0.62f); lineTo(w * 0.08f, h * 0.45f)
                                lineTo(w * 0.05f, h * 0.3f); close()
                            },
                            color = Color(0xFF2E7D32)
                        )

                        // Europe outline (simplified)
                        drawPath(
                            path = Path().apply {
                                moveTo(w * 0.43f, h * 0.15f); lineTo(w * 0.58f, h * 0.12f)
                                lineTo(w * 0.62f, h * 0.22f); lineTo(w * 0.57f, h * 0.32f)
                                lineTo(w * 0.50f, h * 0.35f); lineTo(w * 0.44f, h * 0.28f); close()
                            },
                            color = Color(0xFF2E7D32)
                        )

                        // Africa outline (simplified)
                        drawPath(
                            path = Path().apply {
                                moveTo(w * 0.44f, h * 0.38f); lineTo(w * 0.58f, h * 0.36f)
                                lineTo(w * 0.62f, h * 0.5f); lineTo(w * 0.58f, h * 0.72f)
                                lineTo(w * 0.50f, h * 0.80f); lineTo(w * 0.44f, h * 0.75f)
                                lineTo(w * 0.42f, h * 0.58f); close()
                            },
                            color = Color(0xFF2E7D32)
                        )

                        // Asia outline (simplified)
                        drawPath(
                            path = Path().apply {
                                moveTo(w * 0.60f, h * 0.1f); lineTo(w * 0.95f, h * 0.08f)
                                lineTo(w * 0.98f, h * 0.22f); lineTo(w * 0.90f, h * 0.42f)
                                lineTo(w * 0.75f, h * 0.55f); lineTo(w * 0.62f, h * 0.48f)
                                lineTo(w * 0.60f, h * 0.32f); close()
                            },
                            color = Color(0xFF2E7D32)
                        )

                        // South America outline (simplified)
                        drawPath(
                            path = Path().apply {
                                moveTo(w * 0.22f, h * 0.52f); lineTo(w * 0.35f, h * 0.50f)
                                lineTo(w * 0.38f, h * 0.62f); lineTo(w * 0.32f, h * 0.85f)
                                lineTo(w * 0.24f, h * 0.88f); lineTo(w * 0.20f, h * 0.78f); close()
                            },
                            color = Color(0xFF2E7D32)
                        )

                        // Draw migration routes
                        displayedRoutes.forEach { route ->
                            val routeColor = route.color
                            val path = Path()
                            route.points.forEachIndexed { i, (px, py) ->
                                val x = w * px; val y = h * py
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            }
                            drawPath(path, routeColor.copy(alpha = 0.8f), style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f))))

                            // Arrow at end
                            val last = route.points.last()
                            val secondLast = route.points[route.points.size - 2]
                            val dx = (last.first - secondLast.first) * w
                            val dy = (last.second - secondLast.second) * h
                            drawCircle(routeColor, 6f, Offset(w * last.first, h * last.second))
                        }

                        // Grid lines
                        for (i in 1..3) {
                            drawLine(Color.White.copy(alpha = 0.1f), Offset(0f, h * i / 4), Offset(w, h * i / 4), strokeWidth = 1f)
                        }
                        for (i in 1..5) {
                            drawLine(Color.White.copy(alpha = 0.1f), Offset(w * i / 6, 0f), Offset(w * i / 6, h), strokeWidth = 1f)
                        }
                    }

                    // Legend overlay
                    Card(
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Migration Routes",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filter:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                FilterChip(selected = selectedBird == null, onClick = { selectedBird = null }, label = { Text("All") })
            }
            Spacer(Modifier.height(4.dp))
        }

        // Route list
        items(routes) { route ->
            Card(
                onClick = { selectedBird = if (selectedBird == route.birdName) null else route.birdName },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp).fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedBird == route.birdName) route.color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                ),
                border = if (selectedBird == route.birdName) CardDefaults.outlinedCardBorder() else null
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.size(12.dp).clip(CircleShape).background(route.color)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(route.birdName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(route.from, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Icon(Icons.Filled.ArrowForward, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(route.to, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    TagChip(label = route.category, color = route.color.copy(alpha = 0.1f), textColor = route.color)
                }
            }
        }
    }
}
