package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.viewmodel.MedicineViewModel

@Composable
fun MedicineView(
    navController: NavHostController = rememberNavController(),
    viewModel: MedicineViewModel = viewModel()
) {
    // Mengamati state medicines dari ViewModel
    val medicines by viewModel.medicines.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Logika Refresh saat kembali dari layar Add/Edit
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow("refreshMedicines", false)?.collect { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.getAllMedicines()
                savedStateHandle["refreshMedicines"] = false
            }
        }
    }

    // Refresh otomatis saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        viewModel.getAllMedicines()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (medicines.isEmpty() && !isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No medicines yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = medicines,
                    // PENTING: Gunakan key gabungan agar Compose mendeteksi perubahan konten (stock/name)
                    key = { med -> "${med.id}-${med.name}-${med.stock}" }
                ) { med ->
                    MedicineCard(
                        name = med.name,
                        dosageText = med.dosage,
                        pillsLeftText = if (med.stock > 0) "${med.stock} pills left" else "Out of stock",
                        scheduleTimes = emptyList(),
                        onClick = {
                            navController.navigate("MedicineInfoView/${med.id}")
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(AppView.AddMedicineView.name) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF457AF9),
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Medicine")
        }
    }
}