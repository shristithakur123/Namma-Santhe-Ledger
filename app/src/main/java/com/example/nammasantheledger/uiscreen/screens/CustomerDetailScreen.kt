package com.example.nammasantheledger.uiscreen.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.data.LedgerViewModel
import com.example.nammasantheledger.data.TransactionEntity

private val CdGreen    = Color(0xFF15803D)
private val CdGreen600 = Color(0xFF16A34A)
private val CdBlue     = Color(0xFF2563EB)
private val CdRed      = Color(0xFFDC2626)
private val CdBorder   = Color(0xFFE5E7EB)
private val CdTextDark = Color(0xFF1F2937)
private val CdTextGray = Color(0xFF6B7280)
private val CdWhite    = Color.White
private val CdBgLight  = Color(0xFFF5F5F5)

@Composable
fun CustomerDetailScreen(navController: NavController, viewModel: LedgerViewModel, customerName: String) {
    val customerTx   by viewModel.getCustomerTransactions(customerName).collectAsState()
    val totalCredit  = customerTx.filter { it.isCredit }.sumOf { it.amount }
    val totalPayment = customerTx.filter { !it.isCredit }.sumOf { it.amount }
    val totalDue     = totalCredit - totalPayment
    fun fmt(v: Double) = "₹ ${"%.0f".format(v)}"
    val customerSince = customerTx.lastOrNull()?.let { formatTimestamp(it.createdAt).take(11) } ?: "New Customer"

    Scaffold(containerColor = CdBgLight) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Box(modifier = Modifier.fillMaxWidth().background(CdGreen)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AppBackButton(onClick = { navController.popBackStack() }, tint = CdWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(customerName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CdWhite)
                        Text("Customer Since: $customerSince", fontSize = 12.sp, color = CdWhite.copy(alpha = 0.80f))
                    }
                    Icon(Icons.Default.MoreVert, null, tint = CdWhite, modifier = Modifier.size(22.dp))
                }
            }

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    CdSummaryCard("Total Credit",  fmt(totalCredit),  CdGreen600, Icons.Default.KeyboardArrowUp,   Modifier.weight(1f))
                    CdSummaryCard("Total Payment", fmt(totalPayment), CdBlue,     Icons.Default.KeyboardArrowDown, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(modifier = Modifier.fillMaxWidth().padding(4.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CdWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Total Due", fontSize = 12.sp, color = CdTextGray)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(fmt(totalDue), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = if (totalDue > 0) CdRed else CdGreen600)
                        }
                        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(if (totalDue > 0) CdRed.copy(alpha = 0.10f) else CdGreen600.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                            Icon(if (totalDue > 0) Icons.Default.Warning else Icons.Default.Check, null, tint = if (totalDue > 0) CdRed else CdGreen600, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { navController.navigate("quickEntry") }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = CdGreen)) {
                        Icon(Icons.Default.Add, null, tint = CdWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Credit", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = CdWhite)
                    }
                    Button(onClick = { navController.navigate("quickEntry") }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = CdBlue)) {
                        Icon(Icons.Default.Check, null, tint = CdWhite, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Payment", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CdWhite)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text("Transaction History", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = CdTextDark)
                Spacer(modifier = Modifier.height(10.dp))

                if (customerTx.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CdWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions yet", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CdWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            customerTx.forEachIndexed { index, tx ->
                                CdTxRow(tx)
                                if (index < customerTx.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), thickness = 0.5.dp, color = CdBorder)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CdSummaryCard(title: String, amount: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(4.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = CdWhite), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 11.sp, color = CdTextGray, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(26.dp).clip(CircleShape).background(color.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(amount, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun CdTxRow(tx: TransactionEntity) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.width(80.dp)) { Text(formatTimestamp(tx.createdAt).take(11), fontSize = 11.sp, color = CdTextGray) }
        Box(modifier = Modifier.width(1.dp).height(36.dp).background(CdBorder))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (tx.isCredit) CdGreen600.copy(alpha = 0.10f) else CdBlue.copy(alpha = 0.10f)).padding(horizontal = 8.dp, vertical = 3.dp)) {
                Text(tx.type, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (tx.isCredit) CdGreen600 else CdBlue)
            }
            if (tx.notes.isNotBlank()) { Spacer(modifier = Modifier.height(2.dp)); Text(tx.notes, fontSize = 11.sp, color = CdTextGray) }
        }
        Text("₹ ${"%.0f".format(tx.amount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (tx.isCredit) CdGreen600 else CdBlue)
    }
}