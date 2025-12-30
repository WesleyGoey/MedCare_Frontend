package com.wesley.medcare.ui.view.Medicine

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MedicineView(
    navController: NavHostController = rememberNavController(),
    viewModel: MedicineViewModel = viewModel()
) {
    // SORT: Urutkan ID terbaru ke paling atas
    val rawMedicines by viewModel.medicines.collectAsState()
    val medicines = remember(rawMedicines) { rawMedicines.sortedByDescending { it.id } }
    val isLoading by viewModel.isLoading.collectAsState()

    // SCROLL: Tambahkan state untuk kontrol posisi list
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow("refreshMedicines", false)?.collect { shouldRefresh ->
            if (shouldRefresh) {
                viewModel.getAllMedicines()
                savedStateHandle["refreshMedicines"] = false
                // SCROLL: Otomatis scroll ke atas setelah refresh
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAllMedicines()
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppView.AddMedicineView.name) },
                containerColor = Color(0xFF457AF9),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .offset(y = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Medicine",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            LazyColumn(
                state = listState, // Pasangkan state scroll
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(top = 40.dp)) {
                        Text(
                            text = "Medications",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A2E)
                        )
                        Text(
                            text = "${medicines.size} active medications",
                            fontSize = 16.sp,
                            color = Color(0xFF8A94A6),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (medicines.isEmpty() && !isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxHeight(0.8f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No medicines yet", color = Color(0xFF8A94A6))
                        }
                    }
                } else {
                    items(
                        items = medicines,
                        key = { med -> "${med.id}-${med.name}-${med.stock}" }
                    ) { med ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("${AppView.MedicineInfoView.name}/${med.id}")
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFF457AF9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("💊", fontSize = 24.sp)
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = med.name,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1A1A2E)
                                        )
                                        Text(
                                            text = "${med.dosage} • ${if (med.stock > 0) "${med.stock} pills left" else "Out of stock"}",
                                            fontSize = 14.sp,
                                            color = Color(0xFF5F6368)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Color(0xFFF5F7FA), thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF457AF9),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Daily Schedule",
                                        fontSize = 13.sp,
                                        color = Color(0xFF457AF9),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("08:00", "17:00").forEach { time ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFFECF1FF))
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = time,
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