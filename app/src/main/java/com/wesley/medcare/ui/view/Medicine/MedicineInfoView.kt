package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import kotlinx.coroutines.launch

@Composable
fun MedicineInfoView(
    medicineId: Int,
    navController: NavHostController,
    viewModel: MedicineViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val medicine by viewModel.selectedMedicine.collectAsState()

    LaunchedEffect(medicineId) {
        viewModel.getMedicineById(medicineId)
    }


        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header card with icon, name and dosage
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2F93FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        // placeholder icon
                        Text("💊", modifier = Modifier)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(text = medicine?.name ?: "—", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(text = medicine?.dosage ?: "—", color = Color.Gray)
                }
            }

            // Medication Schedule
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 8.dp),
//                shape = RoundedCornerShape(12.dp),
//                elevation = CardDefaults.cardElevation(4.dp)
//            ) {
//                Column(modifier = Modifier.padding(16.dp)) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = Color(0xFF2F93FF))
//                        Spacer(Modifier.width(8.dp))
//                        Text("Medication Schedule", style = MaterialTheme.typography.titleSmall)
//                    }
//                    Spacer(Modifier.height(12.dp))
//                    Text("Daily Times:", color = Color(0xFF2F93FF))
//                    Spacer(Modifier.height(8.dp))
//                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                        val times = medicine?.schedule ?: emptyList()
//                        if (times.isEmpty()) {
//                            Text("No schedule", color = Color.Gray)
//                        } else {
//                            times.forEach { t ->
//                                Surface(
//                                    shape = RoundedCornerShape(16.dp),
//                                    color = Color(0xFFEFF5FF)
//                                ) {
//                                    Text(
//                                        text = t,
//                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
//                                    )
//                                }
//                            }
//                        }
//                    }
//                    Spacer(Modifier.height(12.dp))
//                    Text(
//                        text = "Dose: ${medicine?.dosage ?: "-"}",
//                        color = Color(0xFF2F93FF)
//                    )
//                    Spacer(Modifier.height(6.dp))
//                    Text(
//                        text = "Notes: ${medicine?.notes ?: "-"}",
//                        color = Color.Gray
//                    )
//                }
//            }

            // Stock Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Inventory, contentDescription = null, tint = Color(0xFF2F93FF))
                        Spacer(Modifier.width(8.dp))
                        Text("Stock Information", style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pills Remaining:", color = Color.Gray)
                        Text("${medicine?.stock ?: 0}", style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Minimum Stock:", color = Color.Gray)
                        Text("${medicine?.minStock ?: "-"}")
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Buttons: Edit & Delete
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        // navigate to edit screen, pass id or medicine
                        navController.navigate("AddMedicineView/${medicine?.id ?: ""}")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F93FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Medication", color = Color.White)
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        medicine?.id?.let { id ->
                            scope.launch {
                                viewModel.deleteMedicine(id)
                                navController.popBackStack()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Medication", color = Color(0xFFD32F2F))
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }