package com.wesley.medcare.ui.view.Medicine

import android.util.Log
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
import com.wesley.medcare.ui.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineView(
    navController: NavController,
    viewModel: MedicineViewModel = viewModel(),
    medicineId: Int
) {
    val context = LocalContext.current
    val medicine by viewModel.selectedMedicine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    // Load medicine data
    LaunchedEffect(medicineId) {
        viewModel.getMedicineById(medicineId)
    }

    // Observe ViewModel state for form fields
    val medicineName by viewModel.medicineName.collectAsState()
    val dosage by viewModel.dosage.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val minStock by viewModel.minStock.collectAsState()
    val selectedType by viewModel.medicineType.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")

    // Handle error messages
    LaunchedEffect(medicine) {
        medicine?.let { m ->
            Log.d("EditMedicineView", "Setting form values from medicine: ${m.name}")
            viewModel.setMedicineName(m.name)
            viewModel.setDosage(m.dosage)
            viewModel.setStock(m.stock)
            viewModel.setMinStock(m.minStock)
            viewModel.setMedicineType(m.type)
            // PENTING: Set notes persis seperti yang diterima dari backend
            // Jangan ubah null menjadi string kosong
            viewModel.setNotes(m.notes)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
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
                    .background(Color.White)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Edit Medication",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Update medication information",
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Basic Information Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF9FAFB)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Basic Information",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Medication Name
                        Text(
                            text = "Medication Name",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = medicineName,
                            onValueChange = { viewModel.setMedicineName(it) },
                            placeholder = { Text("e.g., Paracetamol", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // Dosage
                        Text(
                            text = "Dosage",
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = dosage,
                            onValueChange = { viewModel.setDosage(it) },
                            placeholder = { Text("e.g., 500mg", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )

                        // Stock and Min Stock Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Stock",
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                OutlinedTextField(
                                    value = stock?.toString() ?: "",
                                    onValueChange = {
                                        viewModel.setStock(it.toIntOrNull())
                                    },
                                    placeholder = { Text("30", color = Color(0xFF9CA3AF)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color(0xFFE5E7EB),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "Min. Stock",
                                        fontSize = 14.sp,
                                        color = Color(0xFF6B7280)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Info",
                                        tint = Color(0xFF9CA3AF),
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(start = 4.dp)
                                    )
                                }
                                OutlinedTextField(
                                    value = minStock?.toString() ?: "",
                                    onValueChange = {
                                        viewModel.setMinStock(it.toIntOrNull())
                                    },
                                    placeholder = { Text("5", color = Color(0xFF9CA3AF)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color(0xFFE5E7EB),
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                // Medication Type Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF9FAFB)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Medication Type",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A1A),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        medicineTypes.forEach { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setMedicineType(type) }
                                    .background(
                                        if (selectedType == type) MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.1f
                                        )
                                        else Color.White,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 14.sp,
                                    color = if (selectedType == type) MaterialTheme.colorScheme.primary
                                    else Color(0xFF1A1A1A)
                                )
                                if (selectedType == type) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            if (type != medicineTypes.last()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
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
                        containerColor = Color(0xFFF9FAFB)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "Notes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1A1A1A)
                            )
                            Text(
                                text = " (Optional)",
                                fontSize = 14.sp,
                                color = Color(0xFF9CA3AF)
                            )
                        }

                        OutlinedTextField(
                            value = notes ?: "",
                            onValueChange = { viewModel.setNotes(it) },
                            placeholder = { Text("e.g., Take after meals", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color(0xFFE5E7EB),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            maxLines = 4
                        )
                    }
                }

                // Save Changes Button
                Button(
                    onClick = {
                        Log.d("EditMedicineView", "Update button clicked - id: $medicineId")
                        Log.d("EditMedicineView", "Current values - name: ${viewModel.medicineName.value}, dosage: ${viewModel.dosage.value}, stock: ${viewModel.stock.value}, minStock: ${viewModel.minStock.value}")

                        // Validasi
                        when {
                            viewModel.medicineName.value.isBlank() -> {
                                Toast.makeText(context, "Medicine name cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                            viewModel.dosage.value.isBlank() -> {
                                Toast.makeText(context, "Dosage cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                            viewModel.stock.value == null -> {
                                Toast.makeText(context, "Stock must be a number", Toast.LENGTH_SHORT).show()
                            }
                            viewModel.minStock.value == null -> {
                                Toast.makeText(context, "Minimum stock must be a number", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // Panggil updateMedicine dengan ID
                                viewModel.updateMedicine(medicineId)
                            }
                        }
                    }
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
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
