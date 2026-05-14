package com.example.nammasantheledger.uiscreen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.data.LedgerViewModel

private val LgGreen      = Color(0xFF15803D)
private val LgGreenLight = Color(0xFF16A34A)
private val LgBorder     = Color(0xFFE5E7EB)
private val LgFieldBg    = Color(0xFFF3F4F6)
private val LgTextDark   = Color(0xFF1F2937)
private val LgTextGray   = Color(0xFF6B7280)
private val LgWhite      = Color.White

@Composable
fun LoginScreen(navController: NavController, viewModel: LedgerViewModel) {

    var mobileNumber    by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMsg        by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(LgWhite)) {

        Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Brush.verticalGradient(colors = listOf(Color(0xFF14532D), LgGreenLight))))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(40.dp))

            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(LgWhite), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(LgGreen.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Text("₹", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = LgGreen)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = LgWhite), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Welcome Back! 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LgTextDark, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Sign in to continue", fontSize = 14.sp, color = LgTextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(28.dp))

                    // Mobile
                    OutlinedTextField(
                        value = mobileNumber, onValueChange = { mobileNumber = it; errorMsg = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter Mobile Number", color = LgTextGray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = LgTextGray, modifier = Modifier.size(20.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true, shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LgGreen, unfocusedBorderColor = LgBorder, cursorColor = LgGreen, unfocusedContainerColor = LgFieldBg, focusedContainerColor = LgWhite)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = password, onValueChange = { password = it; errorMsg = "" },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter Password", color = LgTextGray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = LgTextGray, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(if (passwordVisible) Icons.Default.Check else Icons.Default.Close, null, tint = LgTextGray, modifier = Modifier.size(20.dp))
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true, shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LgGreen, unfocusedBorderColor = LgBorder, cursorColor = LgGreen, unfocusedContainerColor = LgFieldBg, focusedContainerColor = LgWhite)
                    )

                    if (errorMsg.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMsg, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login button
                    Button(
                        onClick = {
                            when {
                                mobileNumber.isBlank() -> errorMsg = "Please enter mobile number"
                                password.isBlank()     -> errorMsg = "Please enter password"
                                else -> {
                                    isLoading = true
                                    viewModel.loginUser(
                                        mobile    = mobileNumber.trim(),
                                        password  = password.trim(),
                                        onSuccess = {
                                            isLoading = false
                                            navController.navigate("dashboard") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onError = { msg ->
                                            isLoading = false
                                            errorMsg  = msg
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = LgGreen),
                        enabled  = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = LgWhite, modifier = Modifier.size(20.dp))
                        else Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LgWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("New here? ", fontSize = 14.sp, color = LgTextGray)
                Text("Create Account", fontSize = 14.sp, color = LgGreen, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("register") })
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}