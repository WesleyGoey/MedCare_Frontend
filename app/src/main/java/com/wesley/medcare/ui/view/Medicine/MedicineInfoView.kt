package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.R
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class) // Diperlukan untuk FlowRow
@Composable
fun MedicineInfoView(
    medicineId: Int,
    navController: NavHostController,
    medicineViewModel: MedicineViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val medicine by medicineViewModel.selectedMedicine.collectAsState()
    val isLoading by medicineViewModel.isLoading.collectAsState()
    val successMessage by medicineViewModel.successMessage.collectAsState()
    val allSchedules by scheduleViewModel.schedules.collectAsState()

    val medicineSchedules = allSchedules.filter { it.medicine.id == medicineId }.sortedBy { it.time }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(medicineId) {
        medicineViewModel.getMedicineById(medicineId)
        scheduleViewModel.getSchedulesByDate(LocalDate.now().toString())
    }

    LaunchedEffect(medicine) { scrollState.animateScrollTo(0) }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { BackTopAppBar(title = "Back", onBack = { navController.navigateUp() }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Header Card (Logo & Name) ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(20.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.fillMaxSize())
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(text = medicine?.name ?: "—", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Text(text = medicine?.dosage ?: "—", fontSize = 16.sp, color = Color(0xFF5F6368))
                }
            }

            // --- Schedule Card (Bagian yang Diperbarui dengan FlowRow) ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccessTime, null, tint = Color(0xFF457AF9), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Medication Schedule", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    }
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFECF1FF))
                            .padding(16.dp)
                    ) {
                        Text("Daily Times:", fontSize = 13.sp, color = Color(0xFF457AF9), fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))

                        // MENGGUNAKAN FLOWROW UNTUK MAKSIMAL 3 PER BARIS
                        if (medicineSchedules.isEmpty()) {
                            Text("-", color = Color(0xFF457AF9), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        } else {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                maxItemsInEachRow = 3 // Batas maksimal 3 item per baris
                            ) {
                                medicineSchedules.forEach { schedule ->
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White,
                                        modifier = Modifier.width(75.dp) // Lebar tetap agar rapi 3 kolom
                                    ) {
                                        Text(
                                            text = schedule.time.substring(0, 5),
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFF457AF9),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF457AF9).copy(alpha = 0.2f), thickness = 1.dp)
                        Spacer(Modifier.height(12.dp))

                        val scheduleItems = listOf(
                            "📅" to (if (medicineSchedules.isNotEmpty()) "Every day" else "-"),
                            "💊" to (medicine?.dosage?.let { "Dose: 1 x $it" } ?: "-"),
                            "📝" to (medicine?.notes ?: "-")
                        )

                        scheduleItems.forEach { (icon, label) ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(icon, fontSize = 14.sp); Spacer(Modifier.width(8.dp))
                                Text(text = label, fontSize = 14.sp, color = Color(0xFF457AF9), fontStyle = if (icon == "📝") FontStyle.Italic else FontStyle.Normal)
                            }
                        }
                    }
                }
            }

            // --- Stock Card ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF457AF9)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Inventory2, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Stock Information", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F7FA)).padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pills Remaining:", color = Color(0xFF5F6368))
                            Text(text = "${medicine?.stock ?: 0}", fontWeight = FontWeight.Bold, color = if ((medicine?.stock ?: 0) <= (medicine?.minStock ?: 0)) Color(0xFFFF5A5F) else Color(0xFF1A1A2E))
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFF5F7FA)).padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Minimum Stock:", color = Color(0xFF5F6368))
                            Text(text = "${medicine?.minStock ?: 0}", fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(onClick = { navController.navigate("${AppView.EditMedicineView.name}/${medicine?.id}") }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9), contentColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Edit Medication", fontSize = 16.sp)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { showDeleteDialog = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5A5F)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5A5F))) {
                Icon(Icons.Outlined.Delete, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("Delete Medication", fontSize = 16.sp)
            }
            Spacer(Modifier.height(20.dp))
        }

        // --- Dialog & Loading ---
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Confirm delete", fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to delete this medicine? This action cannot be undone.") },
                confirmButton = { TextButton(onClick = { showDeleteDialog = false; medicineViewModel.deleteMedicine(medicineId) }) { Text("Delete", color = Color(0xFFFF5A5F), fontWeight = FontWeight.Bold) } },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
            )
        }
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF457AF9)) }
        }
    }
}