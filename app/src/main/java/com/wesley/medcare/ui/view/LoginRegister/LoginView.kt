package com.wesley.medcare.ui.view.LoginRegister

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import com.wesley.medcare.R // Pastikan import R untuk mengakses drawable
import com.wesley.medcare.ui.viewmodel.UserViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel,
    onNavigateToHome: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var loginAttempted by remember { mutableStateOf(false) }

    val userState by viewModel.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(userState, isLoading, loginAttempted) {
        if (loginAttempted && !isLoading) {
            when {
                userState.isError && userState.errorMessage != null -> {
                    Toast.makeText(context, userState.errorMessage, Toast.LENGTH_LONG).show()
                    viewModel.resetError()
                    loginAttempted = false
                }
                !userState.isError && userState.errorMessage == null -> {
                    Toast.makeText(context, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    loginAttempted = false
                    onNavigateToHome()
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- PERUBAHAN LOGO DI SINI ---
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color(0xFF457AF9).copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White), // Background diganti putih agar logo terlihat jelas
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.fillMaxSize() // Logo akan mengisi Box dengan sudut melengkung
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MedCare",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF457AF9)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome Back!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sign in to continue",
                fontSize = 15.sp,
                color = Color(0xFF757575)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("your@email.com", color = Color(0xFF757575)) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, null, tint = Color(0xFF457AF9))
                    },
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Enter your password", color = Color(0xFF757575)) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null, tint = Color(0xFF457AF9))
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = null,
                                tint = Color(0xFF8A94A6)
                            )
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

            Spacer(modifier = Modifier.height(28.dp))

            // Sign In Button
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        loginAttempted = true
                        viewModel.login(email, password)
                    } else {
                        Toast.makeText(context, "Isi semua kolom!", Toast.LENGTH_SHORT).show()
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
                    Text("Sign In", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFF0F0F0))
                Text("  OR  ", color = Color(0xFF9E9E9E), fontSize = 12.sp)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFF0F0F0))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Row
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text("Don't have an account? ", color = Color(0xFF757575), fontSize = 14.sp)
                Text(
                    text = "Sign Up",
                    color = Color(0xFF457AF9),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}