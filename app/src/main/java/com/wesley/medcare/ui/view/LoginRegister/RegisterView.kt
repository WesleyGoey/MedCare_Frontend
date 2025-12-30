package com.wesley.medcare.ui.view.LoginRegister

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wesley.medcare.R // Import R untuk akses drawable
import com.wesley.medcare.ui.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterView(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel,
    onNavigateToHome: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var registerAttempted by remember { mutableStateOf(false) }

    val userState by viewModel.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(userState, isLoading, registerAttempted) {
        if (registerAttempted && !isLoading) {
            if (userState.isError) {
                Toast.makeText(context, userState.errorMessage, Toast.LENGTH_LONG).show()
                viewModel.resetError()
                registerAttempted = false
            } else if (userState.errorMessage == null) {
                Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                onNavigateToHome()
            }
        }
    }

    Scaffold(containerColor = Color.White) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 50.dp)
        ) {
            item {
                // --- LOGO BARU DI SINI ---
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(15.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF457AF9).copy(0.4f))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White), // Background putih agar logo biru terlihat bagus
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("MedCare", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF457AF9))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Create Account", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text("Sign up to get started", fontSize = 14.sp, color = Color(0xFF757575))
                Spacer(modifier = Modifier.height(32.dp))

                // --- 1. FULL NAME ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Full Name", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        placeholder = { Text("Enter your full name", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFECF1FF),
                            unfocusedContainerColor = Color(0xFFECF1FF),
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 2. AGE ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Age", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = age, onValueChange = { age = it },
                        placeholder = { Text("Enter your age", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.CalendarToday, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFECF1FF),
                            unfocusedContainerColor = Color(0xFFECF1FF),
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 3. PHONE ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Phone Number", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        placeholder = { Text("Enter phone number", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFECF1FF),
                            unfocusedContainerColor = Color(0xFFECF1FF),
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 4. EMAIL ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Email", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        placeholder = { Text("your@email.com", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Color(0xFF457AF9)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFECF1FF),
                            unfocusedContainerColor = Color(0xFFECF1FF),
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 5. PASSWORD ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        placeholder = { Text("Create a password", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF457AF9)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Color(0xFF8A94A6))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFECF1FF),
                            unfocusedContainerColor = Color(0xFFECF1FF),
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 6. CONFIRM PASSWORD ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Confirm Password", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword, onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm your password", color = Color(0xFF757575)) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF457AF9)) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff, null, tint = Color(0xFF8A94A6))
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFECF1FF),
                            unfocusedContainerColor = Color(0xFFECF1FF),
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- REGISTER BUTTON ---
                Button(
                    onClick = {
                        val ageInt = age.toIntOrNull()
                        if (name.isNotBlank() && email.isNotBlank() && ageInt != null && phone.isNotBlank() && password == confirmPassword) {
                            registerAttempted = true
                            viewModel.register(name, ageInt, phone, email, password)
                        } else {
                            val msg = when {
                                password != confirmPassword -> "Passwords do not match"
                                ageInt == null -> "Invalid age"
                                else -> "Please fill all fields"
                            }
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(10.dp, RoundedCornerShape(14.dp), spotColor = Color(0xFF457AF9)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- SIGN IN LINK ---
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    Text("Already have an account? ", color = Color(0xFF757575), fontSize = 14.sp)
                    Text(
                        text = "Sign In",
                        color = Color(0xFF457AF9),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onSignInClick() }
                    )
                }
            }
        }
    }
}