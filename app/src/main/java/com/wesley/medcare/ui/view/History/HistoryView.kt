package com.wesley.medcare.ui.view.History

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.wesley.medcare.ui.model.History
import com.wesley.medcare.ui.viewmodel.HistoryViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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
    val recentActivity by viewModel.recentActivityList.collectAsState()
    val compliance by viewModel.compliancePercentage.collectAsState()
    val missed by viewModel.missedDosesCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshDashboard()
    }

    if (isLoading && recentActivity.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Teal500)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(GrayBackground),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text("History", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text("This Week (Mon - Sun)", fontSize = 16.sp, color = GrayText)
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SummaryCard(Icons.Default.TrendingUp, Teal500, TealBorder, "$compliance%", "Compliance", Modifier.weight(1f))
                    SummaryCard(Icons.Default.DateRange, Red500, RedBorder, "$missed", "Missed", Modifier.weight(1f))
                }
            }

            item {
                WeeklyComplianceCard(historyData = recentActivity)
            }

            item {
                RecentActivityCard(activityList = recentActivity)
            }
        }
    }
}

@Composable
fun SummaryCard(icon: ImageVector, iconColor: Color, borderColor: Color, value: String, label: String, modifier: Modifier = Modifier) {
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

// ✅ Revisi WeeklyComplianceCard di HistoryView.kt
@Composable
fun WeeklyComplianceCard(historyData: List<History>) {
    // Ambil hanya yyyy-MM-dd agar grouping akurat
    val historyMap = remember(historyData) {
        historyData.groupBy { it.scheduledDate.take(10) }
    }

    val weeklyData = remember(historyMap) {
        val today = LocalDate.now()
        // Standard Senin
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

        (0..6).map { dayOffset ->
            val date = startOfWeek.plusDays(dayOffset.toLong())
            val dateStr = date.toString() // Format yyyy-MM-dd
            val dayLabel = date.format(DateTimeFormatter.ofPattern("EEE", Locale.US))

            val items = historyMap[dateStr] ?: emptyList()
            val percentage = if (items.isNotEmpty()) {
                val taken = items.count { it.status.equals("DONE", ignoreCase = true) }
                (taken * 100) / items.size
            } else 0

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
            Text("Weekly Compliance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText, modifier = Modifier.padding(bottom = 24.dp))
            Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                weeklyData.forEach { (dayLabel, percentage) -> ComplianceBar(day = dayLabel, percentage = percentage) }
            }
            Spacer(modifier = Modifier.height(24.dp))
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
fun RecentActivityCard(activityList: List<History>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText, modifier = Modifier.padding(bottom = 16.dp))

            if (activityList.isEmpty()) {
                Text("No recent activity", color = GrayText, modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 12.dp))
            } else {
                activityList.forEachIndexed { index, history ->
                    val isTaken = history.status.equals("DONE", ignoreCase = true)
                    val timeString = history.scheduledTime
                    val formattedTime = formatDisplayTime(timeString)
                    val formattedDate = formatRelativeDate(history.scheduledDate)

                    ActivityItem(time = "$formattedDate - $formattedTime", medicine = history.medicineName, isTaken = isTaken)
                    if (index < activityList.size - 1) Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ActivityItem(time: String, medicine: String, isTaken: Boolean) {
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
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(iconColor), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ComplianceBar(day: String, percentage: Int) {
    val barColor = when { percentage >= 90 -> Teal500; percentage >= 70 -> Yellow500; else -> Red500 }
    val fillFraction = (percentage / 100f).coerceAtLeast(0.02f)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxHeight()) {
        Box(modifier = Modifier.weight(1f).width(24.dp), contentAlignment = Alignment.BottomCenter) {
            Box(modifier = Modifier.fillMaxHeight(fillFraction).fillMaxWidth().clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)).background(barColor))
        }
        Spacer(modifier = Modifier.height(8.dp))
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

fun formatRelativeDate(isoString: String): String {
    if (isoString.isBlank()) return ""
    return try {
        val date = LocalDate.parse(isoString.take(10))
        val today = LocalDate.now()
        val diff = ChronoUnit.DAYS.between(date, today)
        when {
            diff == 0L -> "Today"
            diff == 1L -> "Yesterday"
            else -> date.format(DateTimeFormatter.ofPattern("MMM d", Locale.US))
        }
    } catch (e: Exception) { isoString.take(10) }
}

fun formatDisplayTime(timeString: String): String {
    if (timeString.isBlank()) return ""
    return try {
        if (timeString.contains("Z") || timeString.contains("T")) {
            val instant = Instant.parse(timeString)
            val formatter = DateTimeFormatter.ofPattern("HH.mm").withZone(ZoneId.of("UTC"))
            formatter.format(instant)
        } else {
            val time = LocalTime.parse(timeString.take(8))
            time.format(DateTimeFormatter.ofPattern("HH.mm"))
        }
    } catch (e: Exception) {
        timeString.take(5).replace(":", ".")
    }
}