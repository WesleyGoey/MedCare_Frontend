package com.wesley.medcare.ui.view.Medicine

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditProfileView(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    val userState by userViewModel.userState.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()

    // State Fields untuk TextField
    var name by remember { mutableStateOf(userState.name) }
    var age by remember {
        mutableStateOf(if (userState.age == 0) "" else userState.age.toString())
    }
    var phone by remember { mutableStateOf(userState.phone) }

    // Password States
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Visibility States
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // LOGIKA 1: Ambil data profil saat layar dibuka
    LaunchedEffect(Unit) {
        userViewModel.getProfile()
    }

    // LOGIKA 2: Sinkronisasi data userState ke TextField (Pre-filled)
    LaunchedEffect(userState) {
        if (userState.name.isNotEmpty()) {
            name = userState.name
            age = if (userState.age == 0) "" else userState.age.toString()
            phone = userState.phone
        }
    }

    // LOGIKA 3: Listener Feedback (Toast & Navigasi)
    LaunchedEffect(userState.errorMessage) {
        userState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            if (message == "Profile updated successfully") {
                navController.popBackStack()
            }
            userViewModel.resetError()
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        focusedContainerColor = Color(0xFFECF1FF),
        unfocusedContainerColor = Color(0xFFECF1FF),
        focusedTextColor = Color(0xFF1A1A2E),
        unfocusedTextColor = Color(0xFF1A1A2E),
        cursorColor = Color(0xFF457AF9)
    )

    Scaffold(
        topBar = {
            BackTopAppBar(title = "Back", onBack = { navController.popBackStack() })
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Edit Profile", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Text(text = "Update your personal information", fontSize = 14.sp, color = Color(0xFF757575), modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFF457AF9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, null, modifier = Modifier.size(50.dp), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SECTION 1: PERSONAL INFO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Full Name", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        placeholder = { Text("Enter your full name", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Age", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = age, onValueChange = { age = it },
                        placeholder = { Text("Enter your age", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.CalendarToday, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Phone Number", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        placeholder = { Text("Enter phone number", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 2: CHANGE PASSWORD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Change Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Current Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = currentPassword, onValueChange = { currentPassword = it },
                        placeholder = { Text("Current Password", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF457AF9)) },
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(if (currentPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Color(0xFF8A94A6))
                            }
                        },
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("New Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword, onValueChange = { newPassword = it },
                        placeholder = { Text("New Password", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF457AF9)) },
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(if (newPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Color(0xFF8A94A6))
                            }
                        },
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Confirm New Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm password", color = Color(0xFF757575), maxLines = 1) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF457AF9)) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Color(0xFF8A94A6))
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    enabled = !isLoading
                ) {
                    Text("Cancel", color = Color(0xFF5F6368), fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        // LOGIKA VALIDASI PASSWORD
                        val isChangingPassword = currentPassword.isNotEmpty() || newPassword.isNotEmpty() || confirmPassword.isNotEmpty()

                        if (isChangingPassword) {
                            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                                Toast.makeText(context, "Semua kolom password harus diisi untuk ganti password", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (newPassword != confirmPassword) {
                                Toast.makeText(context, "Password baru dan konfirmasi tidak cocok", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                        }

                        // Kirim data ke ViewModel
                        userViewModel.updateProfile(
                            name = name,
                            age = age.toIntOrNull() ?: 0,
                            phone = phone,
                            currentPassword = if (isChangingPassword) currentPassword else null,
                            newPassword = if (isChangingPassword) newPassword else null
                        )
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}