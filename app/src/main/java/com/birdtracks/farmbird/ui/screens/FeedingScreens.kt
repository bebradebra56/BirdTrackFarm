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
import com.birdtracks.farmbird.data.db.entity.FeedRecordEntity
import com.birdtracks.farmbird.data.db.entity.FeedStockEntity
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel

@Composable
fun FeedingScreen(navController: NavController, viewModel: AppViewModel) {
    val feedRecords by viewModel.recentFeedRecords.collectAsState()
    val feedStocks by viewModel.feedStocks.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().background(
                    Brush.verticalGradient(listOf(Color(0xFF2E7D32), Color(0xFF43A047), MaterialTheme.colorScheme.background))
                ).padding(24.dp)
            ) {
                Column {
                    Text("Feeding Management", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Track feeding schedules & records", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }

        // Low stock alerts
        val lowStock = feedStocks.filter { it.quantity <= it.minQuantity }
        if (lowStock.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.Warning, null, tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                            Text("Low Stock Alert", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                        }
                        lowStock.forEach { stock ->
                            Text("• ${stock.name}: ${stock.quantity}${stock.unit} remaining", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7B0000))
                        }
                    }
                }
            }
        }

        // Quick actions
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { navController.navigate(Screen.AddFeedRecord.route) },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("Log Feeding")
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.FeedStorage.route) },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Inventory, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp)); Text("Feed Storage")
                }
            }
        }

        // Daily feeding guide
        item {
            SectionCard(title = "Daily Feeding Guide", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                val guides = listOf(
                    Triple("Laying Hens", "120-140g/bird/day", "🐓"),
                    Triple("Broilers", "Ad libitum (free access)", "🐔"),
                    Triple("Ducks", "150-200g/bird/day", "🦆"),
                    Triple("Geese", "200-300g/bird/day", "🦢"),
                    Triple("Quail", "20-25g/bird/day", "🐤")
                )
                guides.forEach { (bird, amount, emoji) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(emoji, style = MaterialTheme.typography.titleSmall)
                            Text(bird, style = MaterialTheme.typography.bodyMedium)
                        }
                        Text(amount, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    }
                    if (bird != guides.last().first) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }

        // Recent feed records
        item {
            SectionHeader(
                "Recent Feed Records",
                action = "Add",
                onAction = { navController.navigate(Screen.AddFeedRecord.route) }
            )
            Spacer(Modifier.height(8.dp))
        }

        if (feedRecords.isEmpty()) {
            item { EmptyState(icon = Icons.Filled.Restaurant, title = "No feed records", message = "Log your first feeding session") }
        } else {
            items(feedRecords, key = { it.id }) { record ->
                FeedRecordItem(record = record, onDelete = { viewModel.deleteFeedRecord(record) })
            }
        }
    }
}

@Composable
private fun FeedRecordItem(record: FeedRecordEntity, onDelete: () -> Unit) {
    var showDelete by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Restaurant, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(record.feedType, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                if (record.groupName.isNotEmpty()) Text(record.groupName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatDate(record.date), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${record.amount}${record.unit}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { showDelete = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
            }
        }
    }
    if (showDelete) {
        AlertDialog(onDismissRequest = { showDelete = false },
            title = { Text("Delete Record?") }, text = { Text("Remove this feed record?") },
            confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
fun AddFeedRecordScreen(navController: NavController, viewModel: AppViewModel) {
    val birdGroups by viewModel.birdGroups.collectAsState()
    val feedStocks by viewModel.feedStocks.collectAsState()

    var feedType by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var selectedGroup by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var groupExpanded by remember { mutableStateOf(false) }
    var feedExpanded by remember { mutableStateOf(false) }
    var feedTypeError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val feedTypes = feedStocks.map { it.name } + listOf("Layer Feed", "Starter Feed", "Grower Feed", "Corn", "Wheat", "Custom")
    val units = listOf("kg", "g", "lb", "oz")

    if (showSuccess) { LaunchedEffect(Unit) { kotlinx.coroutines.delay(1200); navController.popBackStack() } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Filled.Restaurant, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                    Column {
                        Text("Log Feeding", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("Record a feeding session", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        item {
            ExposedDropdownMenuBox(expanded = feedExpanded, onExpandedChange = { feedExpanded = !feedExpanded }) {
                OutlinedTextField(
                    value = feedType, onValueChange = { feedType = it; feedTypeError = false },
                    label = { Text("Feed Type *") }, leadingIcon = { Icon(Icons.Filled.Inventory, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = feedExpanded) },
                    isError = feedTypeError, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(expanded = feedExpanded, onDismissRequest = { feedExpanded = false }) {
                    feedTypes.distinct().forEach { ft ->
                        DropdownMenuItem(text = { Text(ft) }, onClick = { feedType = ft; feedExpanded = false; feedTypeError = false })
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = amountStr, onValueChange = { if (it.matches(Regex("\\d*\\.?\\d*"))) amountStr = it; amountError = false },
                    label = { Text("Amount *") }, isError = amountError,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(2f), shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenuBox(expanded = false, onExpandedChange = {}, modifier = Modifier.weight(1f)) {
                    var unitExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = unitExpanded, onExpandedChange = { unitExpanded = it }) {
                        OutlinedTextField(value = unit, onValueChange = {}, readOnly = true,
                            label = { Text("Unit") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unitExpanded) },
                            modifier = Modifier.menuAnchor(), shape = RoundedCornerShape(12.dp))
                        ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                            units.forEach { u -> DropdownMenuItem(text = { Text(u) }, onClick = { unit = u; unitExpanded = false }) }
                        }
                    }
                }
            }
        }

        item {
            ExposedDropdownMenuBox(expanded = groupExpanded, onExpandedChange = { groupExpanded = !groupExpanded }) {
                OutlinedTextField(
                    value = selectedGroup, onValueChange = {},
                    label = { Text("Bird Group (optional)") }, readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = groupExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Filled.Groups, null) }
                )
                ExposedDropdownMenu(expanded = groupExpanded, onDismissRequest = { groupExpanded = false }) {
                    DropdownMenuItem(text = { Text("All groups / General") }, onClick = { selectedGroup = ""; groupExpanded = false })
                    birdGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Text(getBirdTypeEmoji(group.birdType)); Text(group.name) } },
                            onClick = { selectedGroup = group.name; groupExpanded = false }
                        )
                    }
                }
            }
        }

        item { FormTextField(value = notes, onValueChange = { notes = it }, label = "Notes", singleLine = false, maxLines = 3) }

        item {
            if (showSuccess) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF2E7D32)); Text("Feeding logged!", fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = {
                        feedTypeError = feedType.isEmpty(); amountError = amountStr.isEmpty()
                        if (!feedTypeError && !amountError) {
                            viewModel.addFeedRecord(feedType, amountStr.toFloatOrNull() ?: 0f, unit, System.currentTimeMillis(), selectedGroup, notes)
                            showSuccess = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp)); Text("Save Feed Record", style = MaterialTheme.typography.titleSmall)
                }
            }
        }
    }
}

@Composable
fun FeedStorageScreen(navController: NavController, viewModel: AppViewModel) {
    val feedStocks by viewModel.feedStocks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editTarget by remember { mutableStateOf<FeedStockEntity?>(null) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("${feedStocks.size} feed types in storage", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }

            items(feedStocks, key = { it.id }) { stock ->
                FeedStockCard(stock = stock,
                    onUpdateQuantity = { newQty -> viewModel.updateFeedStock(stock.copy(quantity = newQty, lastUpdated = System.currentTimeMillis())) },
                    onDelete = { viewModel.deleteFeedStock(stock) }
                )
            }

            if (feedStocks.isEmpty()) {
                item { EmptyState(icon = Icons.Filled.Inventory, title = "No feed stocks", message = "Add feed types to track your inventory") }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.End).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) { Icon(Icons.Filled.Add, "Add Stock") }
    }

    if (showAddDialog) {
        AddFeedStockDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, type, qty, unit, minQty ->
                viewModel.addFeedStock(name, type, qty, unit, minQty); showAddDialog = false
            }
        )
    }
}

@Composable
private fun FeedStockCard(stock: FeedStockEntity, onUpdateQuantity: (Float) -> Unit, onDelete: () -> Unit) {
    val isLow = stock.quantity <= stock.minQuantity
    var showEditDialog by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isLow) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(if (isLow) Color(0xFFFFCDD2) else MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Inventory, null, tint = if (isLow) Color(0xFFC62828) else MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(stock.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(stock.feedType, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row {
                    IconButton(onClick = { showEditDialog = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Edit, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showDelete = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${stock.quantity}${stock.unit}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                        color = if (isLow) Color(0xFFC62828) else MaterialTheme.colorScheme.primary)
                    Text("Min: ${stock.minQuantity}${stock.unit}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isLow) TagChip("Low Stock", Color(0xFFFFCDD2), Color(0xFFC62828))
                else TagChip("In Stock", Color(0xFFE8F5E9), Color(0xFF2E7D32))
            }
            Spacer(Modifier.height(8.dp))
            ProgressBar(
                progress = (stock.quantity / (stock.minQuantity * 3)).coerceIn(0f, 1f),
                color = if (isLow) Color(0xFFC62828) else MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showEditDialog) {
        var newQtyStr by remember { mutableStateOf("${stock.quantity}") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update ${stock.name}") },
            text = {
                OutlinedTextField(
                    value = newQtyStr, onValueChange = { if (it.matches(Regex("\\d*\\.?\\d*"))) newQtyStr = it },
                    label = { Text("Quantity (${stock.unit})") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                Button(onClick = { newQtyStr.toFloatOrNull()?.let { onUpdateQuantity(it) }; showEditDialog = false }) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("Cancel") } }
        )
    }
    if (showDelete) {
        AlertDialog(onDismissRequest = { showDelete = false },
            title = { Text("Delete ${stock.name}?") }, text = { Text("Remove this feed stock item?") },
            confirmButton = { TextButton(onClick = { onDelete(); showDelete = false }) { Text("Delete", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
        )
    }
}

@Composable
private fun AddFeedStockDialog(onDismiss: () -> Unit, onAdd: (String, String, Float, String, Float) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Grain") }
    var qtyStr by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("kg") }
    var minQtyStr by remember { mutableStateOf("5") }
    val types = listOf("Grain", "Mixed", "Supplement", "Pellets", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Feed Stock") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Feed Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = qtyStr, onValueChange = { if (it.matches(Regex("\\d*\\.?\\d*"))) qtyStr = it }, label = { Text("Quantity") },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Unit") }, modifier = Modifier.weight(0.6f), shape = RoundedCornerShape(12.dp))
                }
                OutlinedTextField(value = minQtyStr, onValueChange = { if (it.matches(Regex("\\d*\\.?\\d*"))) minQtyStr = it }, label = { Text("Min Quantity (alert threshold)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotEmpty() && qtyStr.isNotEmpty()) onAdd(name, type, qtyStr.toFloatOrNull() ?: 0f, unit, minQtyStr.toFloatOrNull() ?: 5f) },
                enabled = name.isNotEmpty() && qtyStr.isNotEmpty()
            ) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
