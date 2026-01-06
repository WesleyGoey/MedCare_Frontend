import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// --- Main Screen ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {
    Scaffold(
        containerColor = Color(0xFFF8F9FB) // Light gray-blue background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Upcoming Medication",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            UpcomingMedicationCard()

            Spacer(modifier = Modifier.height(16.dp))

            LowStockCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Today's Schedule",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            ScheduleSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Components ---

@Composable
fun HeaderSection() {
    // Dynamic Date Logic
    val dateString = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
    }

    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = "Good Morning!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A2E)
        )
        Text(
            text = dateString,
            fontSize = 16.sp,
            color = Color(0xFF8A94A6),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun UpcomingMedicationCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4B7BE5)), // Blue
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notification",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Metformin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    Text(
                        text = "500mg",
                        fontSize = 14.sp,
                        color = Color(0xFF8A94A6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "Time",
                    tint = Color(0xFFFF5C5C), // Red
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Due in 15 minutes • 2:00 PM",
                    color = Color(0xFFFF5C5C),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun LowStockCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE5E5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5C5C)), // Red Background
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = "Alert",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Low Stock Alert",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Text(
                    text = "Aspirin - Only 3 pills left",
                    fontSize = 14.sp,
                    color = Color(0xFF8A94A6)
                )
            }
        }
    }
}

@Composable
fun ScheduleSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ScheduleItem(
                time = "8:00 AM",
                medName = "Aspirin",
                statusColor = Color(0xFF00C896), // Green
                backgroundColor = Color(0xFFE8FDF5),
                isTaken = true,
                hasDropdown = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            ScheduleItem(
                time = "12:00 PM",
                medName = "Lisinopril",
                statusColor = Color(0xFF00C896),
                backgroundColor = Color(0xFFE8FDF5),
                isTaken = true,
                hasDropdown = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            ScheduleItem(
                time = "2:00 PM",
                medName = "Metformin",
                statusColor = Color(0xFF4B7BE5), // Blue
                backgroundColor = Color(0xFFEBF2FF),
                isTaken = false,
                hasDropdown = false
            )
            Spacer(modifier = Modifier.height(12.dp))
            ScheduleItem(
                time = "8:00 PM",
                medName = "Vitamin D",
                statusColor = Color(0xFFA0A0A0), // Gray
                backgroundColor = Color(0xFFF5F5F5),
                isTaken = false,
                hasDropdown = false
            )
        }
    }
}

@Composable
fun ScheduleItem(
    time: String,
    medName: String,
    statusColor: Color,
    backgroundColor: Color,
    isTaken: Boolean,
    hasDropdown: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status Square
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(statusColor, shape = RoundedCornerShape(2.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "$time - $medName",
            color = Color(0xFF1A1A2E),
            fontSize = 15.sp,
            modifier = Modifier.weight(1f)
        )

        if (hasDropdown) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = Color(0xFF1A1A2E)
            )
        }
    }
}

@Preview
@Composable
fun PreviewDashboard() {
    HomeView()
}