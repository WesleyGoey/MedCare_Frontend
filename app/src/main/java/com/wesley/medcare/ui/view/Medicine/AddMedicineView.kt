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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    navController: NavHostController = rememberNavController(),
    viewModel: MedicineViewModel = viewModel()
) {
    // Reset form saat view dibuka
    LaunchedEffect(Unit) {
        viewModel.clearForm()
        viewModel.resetState()
    }

    val medicineName by viewModel.medicineName.collectAsState()
    val dosage by viewModel.dosage.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val minStock by viewModel.minStock.collectAsState()
    val medicineType by viewModel.medicineType.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val context = LocalContext.current

    // Handle success
    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            viewModel.resetSuccessMessage()
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)
            navController.popBackStack()
        }
    }

    // Handle error
    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            BackTopAppBar(
                title = "Add Medicine",
                onBack = { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Medicine Name
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Medicine Name",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF272B30),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = medicineName,
                    onValueChange = { viewModel.setMedicineName(it) },
                    placeholder = { Text("e.g., Paracetamol") },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF457AF9),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Medicine Type Dropdown
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Medicine Type",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF272B30),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))

                var expanded by remember { mutableStateOf(false) }
                val types = listOf("Tablet", "Capsule", "Syrup", "Injection", "Other")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded && !isLoading }
                ) {
                    OutlinedTextField(
                        value = medicineType,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF457AF9),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    viewModel.setMedicineType(type)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Dosage
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Dosage",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF272B30),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { viewModel.setDosage(it) },
                    placeholder = { Text("e.g., 500mg") },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF457AF9),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Stock and Min Stock Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Stock",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF272B30),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = stock?.toString() ?: "",
                        onValueChange = { viewModel.setStock(it.toIntOrNull()) },
                        placeholder = { Text("0") },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF457AF9),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Min Stock",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF272B30),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = minStock?.toString() ?: "",
                        onValueChange = { viewModel.setMinStock(it.toIntOrNull()) },
                        placeholder = { Text("0") },
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF457AF9),
                            unfocusedBorderColor = Color(0xFFE0E0E0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Notes
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Notes (Optional)",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF272B30),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes ?: "",
                    onValueChange = { viewModel.setNotes(it.ifBlank { null }) },
                    placeholder = { Text("Additional information") },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF457AF9),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = { viewModel.addMedicine() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Add Medicine", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
