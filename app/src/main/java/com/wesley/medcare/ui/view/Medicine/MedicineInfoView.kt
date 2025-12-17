// Kotlin
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
import com.wesley.medcare.ui.view.components.BackTopAppBar
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

    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(medicineId) {
        viewModel.getMedicineById(medicineId)
    }
    Scaffold(
        topBar = {
            BackTopAppBar(onBack = { navController.navigateUp() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header card (name, dosage, icon)
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
                        Text("💊")
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(text = medicine?.name ?: "—", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(text = medicine?.dosage ?: "—", color = Color.Gray)
                }
            }

            // Stock card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Inventory,
                            contentDescription = null,
                            tint = Color(0xFF2F93FF)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Stock Information", style = MaterialTheme.typography.titleSmall)
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Pills Remaining:", color = Color.Gray)
                        Text(
                            "${medicine?.stock ?: 0}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Minimum Stock:", color = Color.Gray)
                        Text("${medicine?.minStock ?: "-"}")
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Edit button
            Button(
                onClick = {
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

            // Delete button triggers confirmation dialog
            OutlinedButton(
                onClick = { showDeleteDialog = true },
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm delete") },
            text = { Text("Are you sure you want to delete this medicine? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteMedicine(medicineId)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)
            navController.popBackStack()
        }
    }

    if (isLoading) {
        Surface {
            CircularProgressIndicator()
        }
    }
}
