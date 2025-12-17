// kotlin
package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    navController: NavHostController,
    viewModel: MedicineViewModel = viewModel()
) {
    // Read fields from ViewModel flows
    val name by viewModel.medicineName.collectAsState()
    val dosage by viewModel.dosage.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val minStock by viewModel.minStock.collectAsState()
    val medType by viewModel.medicineType.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var stockText by remember { mutableStateOf(stock.toString()) }
    var minStockText by remember { mutableStateOf(minStock.toString()) }

    LaunchedEffect(stock, minStock) {
        stockText = stock.toString()
        minStockText = minStock.toString()
    }

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)
            viewModel.resetSuccessMessage()
            navController.popBackStack()
        }
    }

    val medicineTypes =
        listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // On error -> show snackbar and reset state
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.resetState()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Basic Info Card (visual preserved)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Basic Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.setMedicineName(it) },
                        label = { Text("Medication Name") },
                        placeholder = { Text("e.g., Paracetamol") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { viewModel.setDosage(it) },
                        label = { Text("Dosage") },
                        placeholder = { Text("e.g., 500mg") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { new ->
                                val digits = new.filter { it.isDigit() }
                                stockText = digits
                                viewModel.setStock(digits.toIntOrNull() ?: 0) // ensure non-null Int
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            label = { Text("Stock") },
                            placeholder = { Text("30") },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = minStockText,
                            onValueChange = { new ->
                                val digits = new.filter { it.isDigit() }
                                minStockText = digits
                                viewModel.setMinStock(digits.toIntOrNull() ?: 0)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            label = { Text("Min. Stock") },
                            placeholder = { Text("5") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type Card (visual preserved, driven by VM)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Medication Type",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    medicineTypes.chunked(2).forEach { rowTypes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTypes.forEach { t ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(22.dp))
                                        .background(
                                            if (medType == t) Color(0xFF5B9BD5) else Color(
                                                0xFFF0F0F0
                                            )
                                        )
                                        .clickable { viewModel.setMedicineType(t) }
                                        .border(
                                            width = if (medType == t) 2.dp else 0.dp,
                                            color = if (medType == t) Color(0xFF5B9BD5) else Color.Transparent,
                                            shape = RoundedCornerShape(22.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = t,
                                        color = if (medType == t) Color.White else Color.Gray,
                                        fontWeight = if (medType == t) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                            if (rowTypes.size < 2) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Card (driven by VM)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Notes (Optional)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes ?: "",
                        onValueChange = { viewModel.setNotes(it) },
                        placeholder = { Text("e.g., Take after meals") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add Button (uses VM add)
            Button(
                onClick = {
                    viewModel.addMedicine()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B9BD5)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && name.isNotBlank() && dosage.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(text = "Add Medication", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Snackbar host at the bottom
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
        }
    }
}
