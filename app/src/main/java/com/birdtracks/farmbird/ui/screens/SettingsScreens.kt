package com.birdtracks.farmbird.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.FormTextField
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: AppViewModel) {
    val farmName by viewModel.farmName.collectAsState()
    val ownerName by viewModel.ownerName.collectAsState()
    val location by viewModel.location.collectAsState()

    var editFarmName by remember { mutableStateOf(farmName) }
    var editOwnerName by remember { mutableStateOf(ownerName) }
    var editLocation by remember { mutableStateOf(location) }
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(farmName, ownerName, location) {
        editFarmName = farmName; editOwnerName = ownerName; editLocation = location
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2D6A4F), MaterialTheme.colorScheme.background))
                ).padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🌾", style = MaterialTheme.typography.displayMedium)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(farmName.ifEmpty { "My Farm" }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    if (ownerName.isNotEmpty()) Text(ownerName, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        item {
            androidx.compose.material3.Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Farm Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    FormTextField(value = editFarmName, onValueChange = { editFarmName = it; isSaved = false },
                        label = "Farm Name", leadingIcon = Icons.Filled.Home, placeholder = "e.g. Green Valley Farm")

                    FormTextField(value = editOwnerName, onValueChange = { editOwnerName = it; isSaved = false },
                        label = "Owner Name", leadingIcon = Icons.Filled.Person, placeholder = "Your name")

                    FormTextField(value = editLocation, onValueChange = { editLocation = it; isSaved = false },
                        label = "Location", leadingIcon = Icons.Filled.LocationOn, placeholder = "City, Country")

                    if (isSaved) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                            Text("Profile saved!", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.setFarmName(editFarmName)
                                viewModel.setOwnerName(editOwnerName)
                                viewModel.setLocation(editLocation)
                                isSaved = true
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Save, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Save Profile")
                        }
                    }
                }
            }
        }

        item {
            androidx.compose.material3.Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Farm Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))

                    val birdGroups by viewModel.birdGroups.collectAsState()
                    val totalPoultry by viewModel.totalPoultryCount.collectAsState()
                    val observations by viewModel.observations.collectAsState()
                    val eggRecords by viewModel.eggRecords.collectAsState()

                    listOf(
                        Triple(Icons.Filled.Groups, "Bird Groups", "${birdGroups.size}"),
                        Triple(Icons.Filled.Egg, "Total Poultry", "$totalPoultry birds"),
                        Triple(Icons.Filled.NaturePeople, "Wild Bird Sightings", "${observations.size}"),
                        Triple(Icons.Filled.Egg, "Total Eggs Recorded", "${eggRecords.sumOf { it.count }}")
                    ).forEach { (icon, label, value) ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController, viewModel: AppViewModel) {
    val weightUnit by viewModel.weightUnit.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        item {
            SettingsSection(title = "General") {
                SettingsNavItem(icon = Icons.Filled.Person, label = "Profile & Farm", subtitle = "Farm name, location, owner",
                    onClick = { navController.navigate(Screen.Profile.route) })
            }
        }

        item {
            SettingsSection(title = "Units") {
                SettingsSwitchRow(
                    icon = Icons.Filled.Scale,
                    label = "Weight Unit",
                    subtitle = "Currently: $weightUnit",
                    switchLabel = if (weightUnit == "kg") "kg" else "lb",
                    checked = weightUnit == "kg",
                    onCheckedChange = { viewModel.setWeightUnit(if (it) "kg" else "lb") }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsSwitchRow(
                    icon = Icons.Filled.Thermostat,
                    label = "Temperature Unit",
                    subtitle = "Currently: $temperatureUnit",
                    switchLabel = if (temperatureUnit == "°C") "°C" else "°F",
                    checked = temperatureUnit == "°C",
                    onCheckedChange = { viewModel.setTemperatureUnit(if (it) "°C" else "°F") }
                )
            }
        }

        item {
            SettingsSection(title = "Appearance") {
                SettingsSwitchRow(
                    icon = Icons.Filled.DarkMode,
                    label = "Dark Mode",
                    subtitle = if (darkMode) "Dark theme enabled" else "Light theme enabled",
                    switchLabel = "",
                    checked = darkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }
        }

        item {
            SettingsSection(title = "Farm Management") {
                SettingsNavItem(icon = Icons.Filled.Groups, label = "Bird Groups", subtitle = "Manage poultry groups",
                    onClick = { navController.navigate(Screen.BirdGroups.route) })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsNavItem(icon = Icons.Filled.Inventory, label = "Feed Storage", subtitle = "Manage feed stocks",
                    onClick = { navController.navigate(Screen.FeedStorage.route) })
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsNavItem(icon = Icons.Filled.Build, label = "Equipment", subtitle = "Manage farm equipment",
                    onClick = { navController.navigate(Screen.Equipment.route) })
            }
        }

        item {
            SettingsSection(title = "About") {
                SettingsPolicy(icon = Icons.Filled.Policy, label = "Privacy Policy", value = "Tap to read")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsInfoRow(icon = Icons.Filled.Info, label = "App Version", value = "1.0.0")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsInfoRow(icon = Icons.Filled.NaturePeople, label = "Bird Catalog", value = "12 species")
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                SettingsInfoRow(icon = Icons.Filled.Storage, label = "Storage", value = "Local (Room DB)")
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
        androidx.compose.material3.Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
        }
    }
}

@Composable
private fun SettingsNavItem(icon: ImageVector, label: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsSwitchRow(icon: ImageVector, label: String, subtitle: String, switchLabel: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsPolicy(icon: ImageVector, label: String, value: String) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).clickable {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://birdtrackfarm.com/privacy-policy.html"))
            context.startActivity(intent)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SettingsInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        }
        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
