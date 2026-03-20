package com.birdtracks.farmbird.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.components.*
import com.birdtracks.farmbird.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(navController: NavController, viewModel: AppViewModel) {
    val eggRecords by viewModel.eggRecords.collectAsState()
    val feedRecords by viewModel.feedRecords.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val healthRecords by viewModel.healthRecords.collectAsState()

    val calendar = remember { Calendar.getInstance() }
    var displayedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val today = remember { Calendar.getInstance() }

    val monthName = remember(displayedMonth, displayedYear) {
        val cal = Calendar.getInstance().apply { set(Calendar.YEAR, displayedYear); set(Calendar.MONTH, displayedMonth) }
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH).format(cal.time)
    }

    // Get days in month
    val daysInMonth = remember(displayedMonth, displayedYear) {
        val cal = Calendar.getInstance().apply { set(Calendar.YEAR, displayedYear); set(Calendar.MONTH, displayedMonth); set(Calendar.DAY_OF_MONTH, 1) }
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        Pair(firstDayOfWeek, totalDays)
    }

    // Get events for selected day
    val selectedDayEvents = remember(selectedDay, displayedMonth, displayedYear, eggRecords, feedRecords, tasks, healthRecords) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, displayedYear); set(Calendar.MONTH, displayedMonth)
            set(Calendar.DAY_OF_MONTH, selectedDay); set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val dayStart = cal.timeInMillis; val dayEnd = dayStart + 86_400_000L
        buildList {
            eggRecords.filter { it.date in dayStart until dayEnd }.forEach { add(Triple("🥚", "Eggs: ${it.count}", Color(0xFFF9A825))) }
            feedRecords.filter { it.date in dayStart until dayEnd }.forEach { add(Triple("🌾", "Feed: ${it.feedType} ${it.amount}${it.unit}", Color(0xFF2E7D32))) }
            tasks.filter { it.dueDate in dayStart until dayEnd }.forEach { add(Triple(if (it.isCompleted) "✅" else "📋", it.title, if (it.isCompleted) Color(0xFF2E7D32) else Color(0xFF1565C0))) }
            healthRecords.filter { it.date in dayStart until dayEnd }.forEach { add(Triple("🏥", it.issue, Color(0xFFC62828))) }
        }
    }

    // Get days with events (for dot indicators)
    val daysWithEvents = remember(displayedMonth, displayedYear, eggRecords, feedRecords, tasks, healthRecords) {
        buildSet {
            val cal = Calendar.getInstance()
            fun getDayOfMonth(ts: Long): Int {
                cal.timeInMillis = ts; return if (cal.get(Calendar.MONTH) == displayedMonth && cal.get(Calendar.YEAR) == displayedYear) cal.get(Calendar.DAY_OF_MONTH) else -1
            }
            eggRecords.forEach { getDayOfMonth(it.date).let { d -> if (d > 0) add(d) } }
            feedRecords.forEach { getDayOfMonth(it.date).let { d -> if (d > 0) add(d) } }
            tasks.forEach { getDayOfMonth(it.dueDate).let { d -> if (d > 0) add(d) } }
            healthRecords.forEach { getDayOfMonth(it.date).let { d -> if (d > 0) add(d) } }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Month navigation
            Card(modifier = Modifier.padding(16.dp).fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (displayedMonth == 0) { displayedMonth = 11; displayedYear-- } else displayedMonth--
                        }) { Icon(Icons.Filled.ChevronLeft, null) }
                        Text(monthName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        IconButton(onClick = {
                            if (displayedMonth == 11) { displayedMonth = 0; displayedYear++ } else displayedMonth++
                        }) { Icon(Icons.Filled.ChevronRight, null) }
                    }

                    // Weekday headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                            Text(day, modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    // Calendar grid
                    val (firstDayOffset, totalDays) = daysInMonth
                    val totalCells = firstDayOffset + totalDays
                    val rows = (totalCells + 6) / 7
                    repeat(rows) { row ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            repeat(7) { col ->
                                val cellIndex = row * 7 + col
                                val day = cellIndex - firstDayOffset + 1
                                val isValidDay = day in 1..totalDays
                                val isToday = isValidDay && day == today.get(Calendar.DAY_OF_MONTH) &&
                                        displayedMonth == today.get(Calendar.MONTH) && displayedYear == today.get(Calendar.YEAR)
                                val isSelected = isValidDay && day == selectedDay
                                val hasEvents = isValidDay && daysWithEvents.contains(day)

                                Box(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)
                                        .clip(CircleShape)
                                        .background(when { isSelected -> MaterialTheme.colorScheme.primary; isToday -> MaterialTheme.colorScheme.primaryContainer; else -> Color.Transparent })
                                        .clickable(enabled = isValidDay) { if (isValidDay) selectedDay = day },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isValidDay) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                "$day",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = when { isSelected -> Color.White; isToday -> MaterialTheme.colorScheme.primary; else -> MaterialTheme.colorScheme.onSurface }
                                            )
                                            if (hasEvents) {
                                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else MaterialTheme.colorScheme.tertiary))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected day events
        item {
            Text(
                "Events on ${formatShortDate(Calendar.getInstance().apply { set(displayedYear, displayedMonth, selectedDay) }.timeInMillis)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        if (selectedDayEvents.isEmpty()) {
            item {
                Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text("No events for this day", modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(selectedDayEvents) { (emoji, title, color) ->
                Card(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(emoji, style = MaterialTheme.typography.titleMedium)
                        Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Legend
        item {
            Card(modifier = Modifier.padding(16.dp).fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("🥚 Eggs", "🌾 Feed", "📋 Tasks", "🏥 Health").forEach { label ->
                        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
