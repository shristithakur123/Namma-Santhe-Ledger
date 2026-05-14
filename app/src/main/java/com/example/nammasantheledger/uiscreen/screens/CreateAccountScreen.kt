package com.example.nammasantheledger.uiscreen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.data.LedgerViewModel

private val CaGreen      = Color(0xFF15803D)
private val CaGreenDark  = Color(0xFF14532D)
private val CaGreenLight = Color(0xFF16A34A)
private val CaFieldBg    = Color(0xFFF3F4F6)
private val CaBorder     = Color(0xFFE5E7EB)
private val CaTextDark   = Color(0xFF1F2937)
private val CaTextGray   = Color(0xFF6B7280)
private val CaWhite      = Color.White

@Composable
fun CreateAccountScreen(navController: NavController, viewModel: LedgerViewModel) {
    var fullName        by remember { mutableStateOf("") }
    var mobileNumber    by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(CaWhite)) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Brush.verticalGradient(colors = listOf(CaGreenDark, CaGreenLight))))

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                AppBackButton(onClick = { navController.popBackStack() }, tint = CaWhite)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = CaWhite), elevation = CardDefaults.cardElevation(6.dp)) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Create Account", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = CaTextDark, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Join Namma Santhe Ledger", fontSize = 14.sp, color = CaTextGray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(28.dp))

                    CaTextField(fullName,       { fullName = it },       "Full Name",       Icons.Default.Person, KeyboardType.Text)
                    Spacer(modifier = Modifier.height(14.dp))
                    CaTextField(mobileNumber,   { mobileNumber = it },   "Mobile Number",   Icons.Default.Phone,  KeyboardType.Phone)
                    Spacer(modifier = Modifier.height(14.dp))
                    CaPasswordField(password,        { password = it },        "Password",         passwordVisible) { passwordVisible = !passwordVisible }
                    Spacer(modifier = Modifier.height(14.dp))
                    CaPasswordField(confirmPassword, { confirmPassword = it }, "Confirm Password", confirmVisible)  { confirmVisible  = !confirmVisible  }

                    if (errorMessage.isNotEmpty()) { Spacer(modifier = Modifier.height(10.dp)); Text(errorMessage, color = Color(0xFFDC2626), fontSize = 12.sp) }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            when {
                                fullName.isBlank()          -> errorMessage = "Please enter your full name"
                                mobileNumber.isBlank()      -> errorMessage = "Please enter mobile number"
                                password.isBlank()          -> errorMessage = "Please enter a password"
                                password.length < 6         -> errorMessage = "Password must be at least 6 characters"
                                password != confirmPassword -> errorMessage = "Passwords do not match"
                                else -> {
                                    isLoading = true
                                    viewModel.registerUser(
                                        fullName  = fullName.trim(),
                                        mobile    = mobileNumber.trim(),
                                        password  = password.trim(),
                                        onSuccess = { isLoading = false; errorMessage = ""; navController.navigate("login") { popUpTo("register") { inclusive = true } } },
                                        onError   = { msg: String -> isLoading = false; errorMessage = msg }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = CaGreen),
                        enabled  = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = CaWhite, modifier = Modifier.size(20.dp))
                        else Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CaWhite)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", fontSize = 14.sp, color = CaTextGray)
                Text("Login", fontSize = 14.sp, color = CaGreen, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { navController.navigate("login") { popUpTo("register") { inclusive = true } } })
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CaTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, icon: ImageVector, keyboardType: KeyboardType) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = CaTextGray, fontSize = 14.sp) },
        leadingIcon = { Icon(icon, null, tint = CaTextGray, modifier = Modifier.size(20.dp)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType), singleLine = true, shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CaGreen, unfocusedBorderColor = CaBorder, cursorColor = CaGreen, unfocusedContainerColor = CaFieldBg, focusedContainerColor = CaWhite)
    )
}

@Composable
private fun CaPasswordField(value: String, onValueChange: (String) -> Unit, placeholder: String, isVisible: Boolean, onToggle: () -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth(),
        placeholder  = { Text(placeholder, color = CaTextGray, fontSize = 14.sp) },
        leadingIcon  = { Icon(Icons.Default.Lock, null, tint = CaTextGray, modifier = Modifier.size(20.dp)) },
        trailingIcon = { IconButton(onClick = onToggle) { Icon(if (isVisible) Icons.Default.Check else Icons.Default.Close, null, tint = CaTextGray, modifier = Modifier.size(20.dp)) } },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true, shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CaGreen, unfocusedBorderColor = CaBorder, cursorColor = CaGreen, unfocusedContainerColor = CaFieldBg, focusedContainerColor = CaWhite)
    )
}
