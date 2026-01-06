package com.wesley.medcare.ui.view.Medicine

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.R
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class) // Diperlukan untuk FlowRow
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MedicineView(
    navController: NavHostController = rememberNavController(),
    medicineViewModel: MedicineViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    val rawMedicines by medicineViewModel.medicines.collectAsState()
    val medicines = remember(rawMedicines) { rawMedicines.sortedByDescending { it.id } }
    val isLoading by medicineViewModel.isLoading.collectAsState()
    val allSchedules by scheduleViewModel.schedules.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow("refreshMedicines", false)?.collect { shouldRefresh ->
            if (shouldRefresh) {
                medicineViewModel.getAllMedicines()
                scheduleViewModel.getSchedulesByDate(LocalDate.now().toString())
                savedStateHandle["refreshMedicines"] = false
                scope.launch { listState.animateScrollToItem(0) }
            }
        }
    }

    LaunchedEffect(Unit) {
        medicineViewModel.getAllMedicines()
        scheduleViewModel.getSchedulesByDate(LocalDate.now().toString())
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppView.AddMedicineView.name) },
                containerColor = Color(0xFF457AF9),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.offset(y = 20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
            }
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F7FA))) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(top = 20.dp)) {
                        Text("Medications", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                        Text("${medicines.size} active medications", fontSize = 16.sp, color = Color(0xFF8A94A6), modifier = Modifier.padding(top = 4.dp))
                    }
                }

                if (medicines.isEmpty() && !isLoading) {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.8f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No medicines yet", color = Color(0xFF8A94A6))
                        }
                    }
                } else {
                    items(items = medicines, key = { med -> "${med.id}-${med.name}-${med.stock}" }) { med ->
                        val medicineSchedules = allSchedules.filter { it.medicine.id == med.id }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("${AppView.MedicineInfoView.name}/${med.id}") },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                                        Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.fillMaxSize())
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(text = med.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                                        Text(text = "${med.dosage} • ${if (med.stock > 0) "${med.stock} pills left" else "Out of stock"}", fontSize = 14.sp, color = Color(0xFF5F6368))
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF5F7FA), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.AccessTime, null, tint = Color(0xFF457AF9), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Daily Schedule", fontSize = 13.sp, color = Color(0xFF457AF9), fontWeight = FontWeight.Medium)
                                }
                                Spacer(modifier = Modifier.height(12.dp))

                                // MENGGUNAKAN FLOWROW UNTUK OTOMATIS PINDAH BARIS
                                if (medicineSchedules.isEmpty()) {
                                    Text("No schedule set", fontSize = 14.sp, color = Color.Gray)
                                } else {
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        maxItemsInEachRow = 3 // Batas maksimal 3 per baris
                                    ) {
                                        medicineSchedules.forEach { schedule ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(Color(0xFFECF1FF))
                                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = schedule.time.substring(0, 5),
                                                    color = Color(0xFF457AF9),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}