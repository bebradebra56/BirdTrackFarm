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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.SectionCard
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

data class WeatherDay(
    val day: String,
    val high: Int,
    val low: Int,
    val condition: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Int
)

@Composable
fun WeatherScreen(navController: NavController, viewModel: AppViewModel) {
    val location by viewModel.location.collectAsState()

    val month = Calendar.getInstance().get(Calendar.MONTH)
    val (baseTemp, condition, weatherIcon) = remember {
        when (month) {
            in 2..4 -> Triple(14, "Partly Cloudy", "⛅")
            in 5..7 -> Triple(26, "Sunny", "☀️")
            in 8..10 -> Triple(16, "Cloudy", "🌥️")
            else -> Triple(3, "Cold / Frost", "❄️")
        }
    }

    val forecast = remember {
        val days = listOf("Today", "Tomorrow", "Wed", "Thu", "Fri", "Sat", "Sun")
        days.mapIndexed { i, day ->
            val variation = (-3..3).random()
            WeatherDay(
                day = day,
                high = baseTemp + variation + 2,
                low = baseTemp + variation - 5,
                condition = listOf("Sunny", "Partly Cloudy", "Cloudy", "Light Rain", "Clear").random(),
                icon = listOf("☀️", "⛅", "🌥️", "🌧️", "🌤️").random(),
                humidity = (45..75).random(),
                windSpeed = (5..25).random()
            )
        }
    }

    val today = forecast.first()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Main weather card
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFF1565C0), Color(0xFF0288D1), Color(0xFF29B6F6)))
                ).padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Filled.LocationOn, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                        Text(location, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(weatherIcon, fontSize = 72.sp)
                    Text("${baseTemp}°C", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(condition, style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        WeatherStatItem("Humidity", "${today.humidity}%", Icons.Filled.WaterDrop)
                        WeatherStatItem("Wind", "${today.windSpeed} km/h", Icons.Filled.Air)
                        WeatherStatItem("H/L", "${today.high}°/${today.low}°", Icons.Filled.Thermostat)
                    }
                }
            }
        }

        // Forecast
        item {
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("7-Day Forecast", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    forecast.forEach { day ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(day.day, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.width(70.dp))
                            Text(day.icon, style = MaterialTheme.typography.titleMedium)
                            Text(day.condition, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f).padding(horizontal = 8.dp))
                            Text("${day.high}°/${day.low}°", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Farm recommendations
        item {
            SectionCard(title = "Farm Recommendations", modifier = Modifier.padding(horizontal = 16.dp)) {
                val recommendations = getFarmRecommendations(baseTemp, today.humidity)
                recommendations.forEach { rec ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(rec.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) { Text(rec.emoji, style = MaterialTheme.typography.titleSmall) }
                        Column(Modifier.weight(1f)) {
                            Text(rec.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(rec.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Bird migration season
        item {
            SectionCard(title = "Migration Season Status", modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                val (season, birds) = getMigrationStatus(month)
                Text(season, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(6.dp))
                Text(birds, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WeatherStatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
    }
}

data class FarmRecommendation(val emoji: String, val title: String, val desc: String, val color: Color)

private fun getFarmRecommendations(temp: Int, humidity: Int): List<FarmRecommendation> = buildList {
    if (temp > 28) add(FarmRecommendation("🌡️", "Heat Stress Alert", "Ensure extra water access for birds. Consider shade netting for open pens.", Color(0xFFC62828)))
    if (temp < 5) add(FarmRecommendation("❄️", "Cold Weather", "Check insulation in coops. Increase feed amounts. Prevent water from freezing.", Color(0xFF1565C0)))
    if (humidity > 70) add(FarmRecommendation("💧", "High Humidity", "Improve coop ventilation. Watch for respiratory issues. Clean bedding frequently.", Color(0xFF0277BD)))
    if (temp in 15..25 && humidity in 40..65) add(FarmRecommendation("✅", "Ideal Conditions", "Great day for egg laying and outdoor ranging. Optimal conditions for all birds.", Color(0xFF2E7D32)))
    add(FarmRecommendation("🌾", "Feeding Tip", "Adjust feed based on temperature: more energy feed in cold, more water in heat.", Color(0xFFE65100)))
    add(FarmRecommendation("🏠", "Coop Check", "Inspect and clean coops regularly. Good hygiene prevents 80% of diseases.", Color(0xFF558B2F)))
}

private fun getMigrationStatus(month: Int): Pair<String, String> = when (month) {
    in 2..4 -> "🌸 Spring Migration (Mar–May)" to "Watch for: Barn Swallows, White Storks, Common Swifts arriving from Africa. Best time for wild bird observation!"
    in 5..7 -> "☀️ Summer – Breeding Season" to "Resident birds nesting. Swallows, Martins, Swifts actively feeding young. Great time to watch fledglings."
    in 8..10 -> "🍂 Autumn Migration (Aug–Nov)" to "Waterfowl and wading birds moving south. Look for migrating Geese, Cranes, and Ducks. Best time for counting flocks."
    else -> "❄️ Winter – Resident & Winter Visitors" to "Year-round residents plus winter visitors. Watch for Robins, Grey Herons, and wintering ducks in unfrozen water."
}
