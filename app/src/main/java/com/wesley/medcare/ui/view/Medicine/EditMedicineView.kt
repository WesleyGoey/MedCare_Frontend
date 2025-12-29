package com.wesley.medcare.ui.view.Medicine

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineView(
    navController: NavController,
    viewModel: MedicineViewModel = viewModel(),
    medicineId: Int
) {
    val context = LocalContext.current

    // State Observables
    val medicine by viewModel.selectedMedicine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Form Fields
    val medicineName by viewModel.medicineName.collectAsState()
    val dosage by viewModel.dosage.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val minStock by viewModel.minStock.collectAsState()
    val selectedType by viewModel.medicineType.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val medicineTypes =
        listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")

    // 1. Fetch Data Awal
    LaunchedEffect(medicineId) {
        viewModel.getMedicineById(medicineId)
    }

    // 2. Isi Form saat data medicine berhasil diambil
    // Menggunakan key(medicine) agar hanya jalan saat object medicine berubah
    LaunchedEffect(medicine) {
        medicine?.let { m ->
            // Cek apakah form masih kosong/default untuk menghindari overwrite saat user mengetik
            if (viewModel.medicineName.value.isEmpty()) {
                viewModel.setMedicineName(m.name)
                viewModel.setDosage(m.dosage)
                viewModel.setStock(m.stock)
                viewModel.setMinStock(m.minStock)
                viewModel.setMedicineType(m.type)
                viewModel.setNotes(m.notes)
            }
        }
    }

    // 3. Handle Sukses Update
    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()

            // Trigger refresh di layar daftar obat
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)

            viewModel.resetSuccessMessage()
            navController.popBackStack()
        }
    }

    // 4. Handle Error
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = { BackTopAppBar(title = "Back", onBack = { navController.popBackStack() }) }
    ) { paddingValues ->
        if (isLoading && medicine == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F7FA))
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Edit Medication",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A2E),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Update medication information",
                    fontSize = 14.sp,
                    color = Color(0xFF5F6368),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- Form Section ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Basic Information",
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Name
                        Text(
                            "Medication Name",
                            fontSize = 14.sp,
                            color = Color(0xFF5F6368),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = medicineName,
                            onValueChange = { viewModel.setMedicineName(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A2E),
                                unfocusedTextColor = Color(0xFF1A1A2E),
                                cursorColor = Color(0xFF457AF9)
                            )
                        )

                        // Dosage
                        Text(
                            "Dosage",
                            fontSize = 14.sp,
                            color = Color(0xFF5F6368),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { viewModel.setDosage(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A2E),
                                unfocusedTextColor = Color(0xFF1A1A2E),
                                cursorColor = Color(0xFF457AF9)
                            )
                        )

                        // Stock Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Stock",
                                    fontSize = 14.sp,
                                    color = Color(0xFF5F6368),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = stock?.toString() ?: "",
                                    onValueChange = { viewModel.setStock(it.toIntOrNull()) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5),
                                        focusedTextColor = Color(0xFF1A1A2E),
                                        unfocusedTextColor = Color(0xFF1A1A2E),
                                        cursorColor = Color(0xFF457AF9)
                                    )
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Min. Stock",
                                    fontSize = 14.sp,
                                    color = Color(0xFF5F6368),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = minStock?.toString() ?: "",
                                    onValueChange = { viewModel.setMinStock(it.toIntOrNull()) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color(0xFFF5F5F5),
                                        unfocusedContainerColor = Color(0xFFF5F5F5),
                                        focusedTextColor = Color(0xFF1A1A2E),
                                        unfocusedTextColor = Color(0xFF1A1A2E),
                                        cursorColor = Color(0xFF457AF9)
                                    )
                                )
                            }
                        }
                    }
                }

                // Type Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Medication Type",
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        medicineTypes.forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setMedicineType(type) }
                                    .background(
                                        if (selectedType == type) Color(0xFF457AF9)
                                        else Color(0xFFF5F5F5),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = type,
                                    color = if (selectedType == type) Color.White
                                    else Color(0xFF1A1A2E)
                                )
                                if (selectedType == type) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            if (type != medicineTypes.last()) Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                // Notes Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Notes",
                                fontSize = 16.sp,
                                color = Color(0xFF1A1A2E)
                            )
                            Text(
                                text = " (Optional)",
                                fontSize = 14.sp,
                                color = Color(0xFF5F6368)
                            )
                        }
                        OutlinedTextField(
                            value = notes ?: "",
                            onValueChange = { viewModel.setNotes(it) },
                            placeholder = { Text("e.g., Take after meals", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedTextColor = Color(0xFF1A1A2E),
                                unfocusedTextColor = Color(0xFF1A1A2E),
                                cursorColor = Color(0xFF457AF9)
                            ),
                            maxLines = 4
                        )
                    }
                }

                Button(
                    onClick = {
                        if (medicineName.isBlank() || dosage.isBlank() || stock == null || minStock == null) {
                            Toast.makeText(
                                context,
                                "Please fill all required fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.updateMedicine(medicineId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF457AF9),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF457AF9).copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}