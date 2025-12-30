package com.wesley.medcare.ui.view.Medicine

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.wesley.medcare.ui.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileView(
    navController: NavHostController = rememberNavController(),
    userViewModel: UserViewModel = viewModel()
) {
    val userState by userViewModel.userState.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    var pushEnabled by remember { mutableStateOf(true) }
    var stockEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        userViewModel.getProfile()
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // --- SECTION HEADER ---
            Column(modifier = Modifier.padding(top = 40.dp, bottom = 24.dp)) {
                Text(
                    text = "Profile",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
            }

            // --- BLUE PROFILE CARD (Rekomendasi Urutan Terbaik) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF457AF9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { /* Edit Action */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Edit, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(90.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(25.dp), color = Color(0xFF457AF9), strokeWidth = 3.dp)
                            } else {
                                Icon(Icons.Default.Person, null, modifier = Modifier.padding(18.dp), tint = Color(0xFF457AF9))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 1. NAME (Identitas Utama)
                        Text(text = userState.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)

                        // 2. AGE (Data Medis Penting)
                        Text(
                            text = "${userState.age} years old",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        // 3. PHONE (Kontak Darurat Utama)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                            Icon(Icons.Default.Phone, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = userState.phone, fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                        }

                        // 4. EMAIL (Data Akun/Pelengkap)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(Icons.Default.Email, null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = userState.email, fontSize = 13.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SETTINGS LIST ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Alarm Sound
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF457AF9)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Alarm, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text("Alarm Sound", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Text("Default Alarm", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFE0E0E0), modifier = Modifier.size(24.dp))
                    }

                    HorizontalDivider(color = Color(0xFFF5F7FA), thickness = 1.dp)

                    // Notification Sound
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF457AF9)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.NotificationsActive, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text("Notification Sound", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Text("Gentle Chime", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFE0E0E0), modifier = Modifier.size(24.dp))
                    }

                    HorizontalDivider(color = Color(0xFFF5F7FA), thickness = 1.dp)

                    // Push Notifications Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF457AF9)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Notifications, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text("Push Notifications", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Text("Get medication alerts", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                        Switch(
                            checked = pushEnabled,
                            onCheckedChange = { pushEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2FB6A3),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFE0E0E0),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF5F7FA), thickness = 1.dp)

                    // Low Stock Alerts Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF457AF9)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Inventory2, null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                            Text("Low Stock Alerts", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A2E))
                            Text("Alert when running low", fontSize = 12.sp, color = Color(0xFF757575))
                        }
                        Switch(
                            checked = stockEnabled,
                            onCheckedChange = { stockEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF2FB6A3),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFE0E0E0),
                                uncheckedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- LOGOUT BUTTON ---
            Button(
                onClick = {
                    userViewModel.resetError()
                    navController.navigate("login") { popUpTo(0) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, Color(0xFFFFEBEB), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, null, tint = Color(0xFFFF5A5F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Log Out", color = Color(0xFFFF5A5F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}