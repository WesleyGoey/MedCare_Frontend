package com.wesley.medcare.ui.view.LoginRegister

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wesley.medcare.ui.viewmodel.UserViewModel

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

    // Handle registration success/error
    LaunchedEffect(userState, isLoading, registerAttempted) {
        if (registerAttempted && !isLoading) {
            when {
                userState.isError && userState.errorMessage != null -> {
                    Toast.makeText(context, userState.errorMessage, Toast.LENGTH_LONG).show()
                    viewModel.resetError()
                    registerAttempted = false
                }
                !userState.isError && userState.errorMessage == null -> {
                    Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    registerAttempted = false
                    onNavigateToHome()
                }
            }
        }
    }



    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 360.dp)
                    .wrapContentHeight()
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF457AF9))
                        .shadow(6.dp, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "MedCare",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF457AF9)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Create Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B1220)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sign up to get started",
                    fontSize = 13.sp,
                    color = Color(0xFF9AA3AE)
                )

                Spacer(Modifier.height(12.dp))

                // Name
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Full Name", fontWeight = FontWeight.SemiBold, color = Color(0xFF272B30))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F7FF))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF457AF9)
                        )
                        Spacer(Modifier.width(10.dp))
                        TextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Enter your full name") },
                            enabled = !isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color(0xFF457AF9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Age
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Age", fontWeight = FontWeight.SemiBold, color = Color(0xFF272B30))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F7FF))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = Color(0xFF457AF9)
                        )
                        Spacer(Modifier.width(10.dp))
                        TextField(
                            value = age,
                            onValueChange = { age = it },
                            placeholder = { Text("Your age") },
                            enabled = !isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color(0xFF457AF9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Phone
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Phone", fontWeight = FontWeight.SemiBold, color = Color(0xFF272B30))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F7FF))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Color(0xFF457AF9)
                        )
                        Spacer(Modifier.width(10.dp))
                        TextField(
                            value = phone,
                            onValueChange = { phone = it },
                            placeholder = { Text("Your phone number") },
                            enabled = !isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color(0xFF457AF9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Email
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Email", fontWeight = FontWeight.SemiBold, color = Color(0xFF272B30))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F7FF))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = Color(0xFF457AF9)
                        )
                        Spacer(Modifier.width(10.dp))
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("your@email.com") },
                            enabled = !isLoading,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color(0xFF457AF9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Password", fontWeight = FontWeight.SemiBold, color = Color(0xFF272B30))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F7FF))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF457AF9)
                        )
                        Spacer(Modifier.width(10.dp))
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Create a password") },
                            enabled = !isLoading,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color(0xFF457AF9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Confirm Password
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Confirm Password", fontWeight = FontWeight.SemiBold, color = Color(0xFF272B30))
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF0F7FF))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF457AF9)
                        )
                        Spacer(Modifier.width(10.dp))
                        TextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = { Text("Confirm your password") },
                            enabled = !isLoading,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                cursorColor = Color(0xFF457AF9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        // Validation
                        when {
                            name.isBlank() -> Toast.makeText(context, "Nama harus diisi", Toast.LENGTH_SHORT).show()
                            age.isBlank() -> Toast.makeText(context, "Umur harus diisi", Toast.LENGTH_SHORT).show()
                            phone.isBlank() -> Toast.makeText(context, "Nomor telepon harus diisi", Toast.LENGTH_SHORT).show()
                            email.isBlank() -> Toast.makeText(context, "Email harus diisi", Toast.LENGTH_SHORT).show()
                            password.isBlank() -> Toast.makeText(context, "Password harus diisi", Toast.LENGTH_SHORT).show()
                            confirmPassword.isBlank() -> Toast.makeText(context, "Konfirmasi password harus diisi", Toast.LENGTH_SHORT).show()
                            password != confirmPassword -> Toast.makeText(context, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                            password.length < 6 -> Toast.makeText(context, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
                            else -> {
                                val ageInt = age.toIntOrNull()
                                if (ageInt == null) {
                                    Toast.makeText(context, "Umur harus berupa angka", Toast.LENGTH_SHORT).show()
                                } else {
                                    registerAttempted = true
                                    viewModel.register(name, ageInt, phone, email, password)
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                color = Color(0xFF457AF9),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Create Account",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE6E6E6))
                    Text("  OR  ", color = Color(0xFF9AA3AE), fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE6E6E6))
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Already have an account? ", color = Color(0xFF9AA3AE))
                    Text(
                        text = "Sign In",
                        color = Color(0xFF457AF9),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSignInClick() }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RegisterPreview() {
    // Preview without ViewModel
}
