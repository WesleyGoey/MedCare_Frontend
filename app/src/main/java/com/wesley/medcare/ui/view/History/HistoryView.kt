package com.wesley.medcare.ui.view.History

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.model.History // IMPORT YOUR MODEL
import com.wesley.medcare.ui.viewmodel.HistoryViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// ... (Color definitions remain the same: Teal500, etc.) ...
// Color Palette
private val Teal500 = Color(0xFF2FB6A3)
private val TealBorder = Color(0xFF2FB6A3).copy(alpha = 0.25f)
private val TealLightBg = Color(0xFFF0F9F8)

private val Red500 = Color(0xFFFF5A5F)
private val RedBorder = Color(0xFFFF5A5F).copy(alpha = 0.25f)
private val RedLightBg = Color(0xFFFFF5F5)

private val Yellow500 = Color(0xFFFFC107)
private val GrayText = Color(0xFF8A94A6)
private val DarkText = Color(0xFF1A1A2E)
private val GrayBackground = Color(0xFFF5F7FA)

@Composable
fun HistoryView(
    navController: NavHostController,
    viewModel: HistoryViewModel = viewModel()
) {
    // Collect State
    // NOTE: Ensure your ViewModel now exposes StateFlow<List<History>>
    val recentActivity by viewModel.recentActivityList.collectAsState()
    val compliance by viewModel.compliancePercentage.collectAsState()
    val missed by viewModel.missedDosesCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.refreshDashboard() }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Teal500)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(GrayBackground),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ... Header & SummaryCards (Same as before) ...
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("History", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text("This Week", fontSize = 16.sp, color = GrayText)
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SummaryCard(Icons.Default.TrendingUp, Teal500, TealBorder, "$compliance%", "Compliance", Modifier.weight(1f))
                    SummaryCard(Icons.Default.DateRange, Red500, RedBorder, "$missed", "Missed", Modifier.weight(1f))
                }
            }

            item {
                // 1. Pass the raw history list to the chart
                // The chart will calculate percentages internally
                WeeklyComplianceCard(historyData = recentActivity)
            }

            item {
                // 2. Pass the raw history list to the activity list
                RecentActivityCard(
                    activityList = recentActivity
                )
            }
        }
    }
}

// ... (SummaryCard remain the same) ...
@Composable
fun SummaryCard(
    icon: ImageVector, iconColor: Color, borderColor: Color, value: String, label: String, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = iconColor)
            Text(text = label, fontSize = 14.sp, color = GrayText)
        }
    }
}

@Composable
fun WeeklyComplianceCard(historyData: List<History>) {
    // 1. Prepare Data Map: Group by Date (Key = "2026-01-06")
    // We do this first for fast lookup inside the loop
    val historyMap = remember(historyData) {
        historyData.groupBy {
            // Fix: Handle full timestamps like "2026-01-06T09:00:00Z" -> "2026-01-06"
            it.scheduledDate.take(10)
        }
    }

    // 2. Generate the Fixed Week (Mon - Sun)
    val weeklyData = remember(historyMap) {
        val today = LocalDate.now()
        // Find the Monday of the current week (anchors the chart)
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        // Create exactly 7 bars starting from Monday
        (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            val dateStr = date.toString() // e.g. "2026-01-06"

            // Format label: "MON", "TUE"
            val dayLabel = date.format(DateTimeFormatter.ofPattern("EEE", Locale.US)).uppercase()

            // Lookup data for this specific day
            val items = historyMap[dateStr] ?: emptyList()

            // Calculate Percentage
            val percentage = if (items.isNotEmpty()) {
                val taken = items.count { it.status.equals("DONE", ignoreCase = true) }
                (taken * 100) / items.size
            } else {
                0
            }

            // Return pair: Label + Value
            dayLabel to percentage
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Weekly Compliance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // RENDER THE CHART
            Row(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Loop through our fixed 7 days (Mon-Sun) instead of the raw data list
                weeklyData.forEach { (dayLabel, percentage) ->
                    ComplianceBar(day = dayLabel, percentage = percentage)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Legend (Kept the same)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                LegendItem(color = Teal500, label = "90-100%")
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = Yellow500, label = "70-89%")
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = Red500, label = "< 70%")
            }
        }
    }
}

@Composable
fun RecentActivityCard(
    activityList: List<History>
) {
    Card(
        // ... (modifiers and colors remain the same)
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText, modifier = Modifier.padding(bottom = 16.dp))

            if (activityList.isEmpty()) {
                Text("No recent activity", color = GrayText, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                activityList.take(5).forEachIndexed { index, history ->
                    // 1. Determine Status
                    val isTaken = history.status.equals("DONE", ignoreCase = true) ||
                            history.status.equals("TAKEN", ignoreCase = true)

                    // 2. Format Time: Pick 'timeTaken' if done, otherwise 'scheduledTime'
                    val rawTime = if (isTaken && history.timeTaken.isNotEmpty()) {
                        history.timeTaken
                    } else {
                        history.scheduledTime
                    }
                    val formattedTime = formatDisplayTime(rawTime)

                    // 3. Format Date
                    val formattedDate = formatDisplayDate(history.scheduledDate)

                    ActivityItem(
                        // Combine them nicely: "Today • 4:04 PM"
                        time = "$formattedDate • $formattedTime",
                        medicine = history.medicineName,
                        isTaken = isTaken
                    )

                    if (index < activityList.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}
@Composable
fun ActivityItem(
    time: String,
    medicine: String,
    isTaken: Boolean
) {
    val bgColor = if (isTaken) TealLightBg else RedLightBg
    val borderColor = if (isTaken) TealBorder else RedBorder
    val iconColor = if (isTaken) Teal500 else Red500
    val icon = if (isTaken) Icons.Default.Check else Icons.Default.Close

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = time, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = medicine, fontSize = 14.sp, color = GrayText)
        }

        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ComplianceBar(day: String, percentage: Int) {
    val barColor = when {
        percentage >= 90 -> Teal500
        percentage >= 70 -> Yellow500
        else -> Red500
    }

    // Convert integer percentage (e.g., 80) to float fraction (0.8f)
    // Ensure we show at least a tiny sliver (0.02f) even if 0%, so it's visible
    val fillFraction = (percentage / 100f).coerceAtLeast(0.02f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        // Remove fillMaxHeight() here; let the weight handle the height
        modifier = Modifier.fillMaxHeight()
    ) {
        // 1. The "Track" (Invisible container that holds the bar)
        Box(
            modifier = Modifier
                .weight(1f) // Takes up all available space above the text
                .width(22.dp),
            contentAlignment = Alignment.BottomCenter // Important: Anchors bar to bottom
        ) {
            // 2. The Actual Colored Bar
            Box(
                modifier = Modifier
                    .fillMaxHeight(fillFraction) // <--- THIS FIXES THE HEIGHT
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Day Label
        Text(text = day, fontSize = 12.sp, color = GrayText)
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 12.sp, color = GrayText)
    }
}

fun formatDisplayDate(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val instant = Instant.parse(isoString)

        // USE LOCAL TIMEZONE HERE
        val zoneId = ZoneId.systemDefault()

        val date = LocalDateTime.ofInstant(instant, zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> DateTimeFormatter.ofPattern("MMM d", Locale.US).format(date)
        }
    } catch (e: Exception) {
        isoString.take(10)
    }
}

fun formatDisplayTime(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val instant = Instant.parse(isoString)

        val zoneId = ZoneId.of("UTC")

        val dateTime = LocalDateTime.ofInstant(instant, zoneId)
        DateTimeFormatter.ofPattern("h:mm a", Locale.US).format(dateTime)
    } catch (e: Exception) {
        isoString.takeLast(8)
    }
}