package com.example.nammasantheledger.uiscreen.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.data.LedgerViewModel

private val WaGreen    = Color(0xFF15803D)
private val WaWhatsApp = Color(0xFF25D366)
private val WaRed      = Color(0xFFDC2626)
private val WaBorder   = Color(0xFFE5E7EB)
private val WaTextDark = Color(0xFF1F2937)
private val WaTextGray = Color(0xFF6B7280)
private val WaWhite    = Color.White
private val WaBgLight  = Color(0xFFF9FAFB)

@Composable
fun WhatsAppReminderScreen(navController: NavController, viewModel: LedgerViewModel) {
    WaReminderContent(navController, viewModel, "")
}

@Composable
fun WhatsAppReminderScreenWithCustomer(navController: NavController, viewModel: LedgerViewModel, customerName: String) {
    WaReminderContent(navController, viewModel, customerName)
}

@Composable
private fun WaReminderContent(navController: NavController, viewModel: LedgerViewModel, initialCustomer: String) {
    val context         = LocalContext.current
    val customers       by viewModel.customers.collectAsState()
    val allTransactions by viewModel.transactions.collectAsState()

    var selectedCustomer by remember { mutableStateOf(initialCustomer) }
    var phone            by remember { mutableStateOf("") }
    var showPicker       by remember { mutableStateOf(false) }
    var errorMsg         by remember { mutableStateOf("") }

    val outstanding = remember(selectedCustomer, allTransactions.size) {
        if (selectedCustomer.isEmpty()) "₹ 0"
        else {
            val txList  = allTransactions.filter { it.customerName == selectedCustomer }
            val credit  = txList.filter { it.isCredit }.sumOf { it.amount }
            val payment = txList.filter { !it.isCredit }.sumOf { it.amount }
            val due     = credit - payment
            if (due > 0) "₹ ${"%.0f".format(due)}" else "₹ 0"
        }
    }

    var message by remember(selectedCustomer, outstanding) {
        mutableStateOf(
            if (selectedCustomer.isEmpty()) ""
            else "Hi $selectedCustomer,\nThis is a reminder for your pending amount of $outstanding.\nPlease clear at the earliest.\nThank you!"
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(WaBgLight)) {

        Box(modifier = Modifier.fillMaxWidth().background(WaGreen)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                AppBackButton(onClick = { navController.popBackStack() }, tint = WaWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Reminder", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WaWhite)
            }
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {

            Text("Customer", fontSize = 13.sp, color = WaTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WaWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(WaGreen.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = WaGreen, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(if (selectedCustomer.isEmpty()) "Select a customer" else selectedCustomer, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = if (selectedCustomer.isEmpty()) WaTextGray else WaTextDark)
                    }
                    Text("Change", fontSize = 13.sp, color = WaGreen, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { showPicker = !showPicker; errorMsg = "" })
                }
            }

            if (showPicker && customers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WaWhite), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column {
                        customers.forEachIndexed { index, customer ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { selectedCustomer = customer.name; showPicker = false; errorMsg = "" }.padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null, tint = WaTextGray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(customer.name, fontSize = 14.sp, color = WaTextDark)
                            }
                            if (index < customers.lastIndex) HorizontalDivider(color = WaBorder, thickness = 0.5.dp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Phone Number", fontSize = 13.sp, color = WaTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone, onValueChange = { phone = it; errorMsg = "" },
                modifier    = Modifier.fillMaxWidth(),
                placeholder = { Text("+91 98765 43210", color = WaTextGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Phone, null, tint = WaTextGray, modifier = Modifier.size(20.dp)) },
                singleLine  = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = WaGreen, unfocusedBorderColor = WaBorder, cursorColor = WaGreen, unfocusedContainerColor = WaWhite, focusedContainerColor = WaWhite)
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text("Outstanding Amount", fontSize = 13.sp, color = WaTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WaWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = WaRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(outstanding, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = WaRed)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Message Preview", fontSize = 13.sp, color = WaTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = message, onValueChange = { message = it },
                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 130.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = OutlinedTextFieldDefaults.colors(focusedBorderColor = WaWhatsApp, unfocusedBorderColor = WaBorder, cursorColor = WaGreen, unfocusedContainerColor = Color(0xFFDCF8C6), focusedContainerColor = Color(0xFFDCF8C6))
            )

            if (errorMsg.isNotEmpty()) { Spacer(modifier = Modifier.height(8.dp)); Text(errorMsg, color = WaRed, fontSize = 12.sp) }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    when {
                        selectedCustomer.isEmpty() -> errorMsg = "Please select a customer"
                        phone.isBlank()            -> errorMsg = "Please enter phone number"
                        else -> {
                            try {
                                val cleanPhone = phone.trim().replace(" ", "").replace("+", "").replace("-", "")
                                val uri    = Uri.parse("https://wa.me/$cleanPhone?text=${Uri.encode(message)}")
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                                context.startActivity(intent)
                            } catch (e: Exception) { errorMsg = "Could not open WhatsApp. Please install it." }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = WaWhatsApp)
            ) {
                Icon(Icons.Default.Send, null, tint = WaWhite, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Send on WhatsApp", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = WaWhite)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Opens WhatsApp with pre-filled message", fontSize = 12.sp, color = WaTextGray)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}