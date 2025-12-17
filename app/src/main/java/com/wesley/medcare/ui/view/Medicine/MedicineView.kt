package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
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
    val medicines by viewModel.medicines.collectAsState()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            ?.getStateFlow("refreshMedicines", false)
            ?.collect { shouldRefresh ->
                if (shouldRefresh) {
                    viewModel.getAllMedicines()
                    savedStateHandle["refreshMedicines"] = false
                }
            }
    }

    LaunchedEffect(Unit) {
        if (medicines.isEmpty()) {
            viewModel.getAllMedicines()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (medicines.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No medicines yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medicines, key = { it.id }) { med ->
                    MedicineCard(
                        name = med.name,
                        dosageText = med.dosage,
                        pillsLeftText = if (med.stock > 0) "${med.stock} pills left" else "Out of stock",
                        scheduleTimes = emptyList(),
                        onClick = {
                            // Navigate ke MedicineInfoView dengan medicineId
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
            containerColor = Color(0xFF2F93FF),
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Medicine")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MedicinePreview() {
    MedicineView()
}
