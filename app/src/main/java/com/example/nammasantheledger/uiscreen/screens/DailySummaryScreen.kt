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
import java.text.SimpleDateFormat
import java.util.*

private val DsGreen    = Color(0xFF15803D)
private val DsGreen600 = Color(0xFF16A34A)
private val DsBlue     = Color(0xFF2563EB)
private val DsPurple   = Color(0xFF7C3AED)
private val DsRed      = Color(0xFFDC2626)
private val DsBorder   = Color(0xFFE5E7EB)
private val DsTextDark = Color(0xFF1F2937)
private val DsTextGray = Color(0xFF6B7280)
private val DsWhite    = Color.White
private val DsBgLight  = Color(0xFFF5F5F5)

@Composable
fun DailySummaryScreen(navController: NavController, viewModel: LedgerViewModel) {

    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    val dateFormat   = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val selectedDateStr = dateFormat.format(selectedDate.time)

    val allTransactions by viewModel.transactions.collectAsState()

    // Filter for selected date
    val dayTransactions = allTransactions.filter { tx ->
        formatTimestamp(tx.createdAt).take(11).trim() == selectedDateStr.trim()
    }

    val totalSales    = dayTransactions.filter { it.isCredit }.sumOf { it.amount }
    val totalPayments = dayTransactions.filter { !it.isCredit }.sumOf { it.amount }
    val todayProfit   = totalSales - totalPayments
    val allCredit     = allTransactions.filter { it.isCredit }.sumOf { it.amount }
    val allPayment    = allTransactions.filter { !it.isCredit }.sumOf { it.amount }
    val pendingDues   = (allCredit - allPayment).coerceAtLeast(0.0)

    fun fmt(v: Double) = "₹ ${"%.0f".format(v)}"

    Scaffold(bottomBar = { BottomBar(navController) }, containerColor = DsBgLight) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Box(modifier = Modifier.fillMaxWidth().background(DsGreen)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Menu, null, tint = DsWhite, modifier = Modifier.size(24.dp))
                    Text("Daily Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DsWhite)
                    Icon(Icons.Default.Notifications, null, tint = DsWhite, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(12.dp)) {

                // Date navigator
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = DsWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(DsBgLight).clickable {
                            val cal = selectedDate.clone() as Calendar; cal.add(Calendar.DAY_OF_YEAR, -1); selectedDate = cal
                        }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.KeyboardArrowLeft, null, tint = DsTextDark, modifier = Modifier.size(22.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.DateRange, null, tint = DsGreen, modifier = Modifier.size(20.dp))
                            Text(selectedDateStr, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DsTextDark)
                        }
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(DsBgLight).clickable {
                            val cal = selectedDate.clone() as Calendar; cal.add(Calendar.DAY_OF_YEAR, 1); selectedDate = cal
                        }, contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = DsTextDark, modifier = Modifier.size(22.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stat cards
                Row(modifier = Modifier.fillMaxWidth()) {
                    DsSummaryCard("Total Sales (Credit)", fmt(totalSales),    DsGreen600, Icons.Default.KeyboardArrowUp,   Modifier.weight(1f))
                    DsSummaryCard("Total Payments",       fmt(totalPayments), DsBlue,     Icons.Default.KeyboardArrowDown, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    DsSummaryCard("Today's Profit", fmt(todayProfit), DsPurple, Icons.Default.Star,    Modifier.weight(1f))
                    DsSummaryCard("Pending Dues",   fmt(pendingDues), DsRed,    Icons.Default.Warning, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Recent Transactions", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DsTextDark, modifier = Modifier.padding(bottom = 10.dp))

                if (dayTransactions.isEmpty()) {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = DsWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.fillMaxWidth().padding(40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions on $selectedDateStr", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = DsWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            dayTransactions.forEachIndexed { index, tx ->
                                DsTxRow(tx, navController)
                                if (index < dayTransactions.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), thickness = 0.5.dp, color = DsBorder)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun DsSummaryCard(title: String, amount: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier.padding(4.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = DsWhite), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 11.sp, color = DsTextGray, modifier = Modifier.weight(1f))
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
fun DsTxRow(tx: TransactionEntity, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().clickable { navController.navigate("customerDetail/${tx.customerName}") }.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(if (tx.isCredit) DsGreen600.copy(alpha = 0.12f) else DsBlue.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Person, null, tint = if (tx.isCredit) DsGreen600 else DsBlue, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.customerName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DsTextDark)
            Text(tx.type, fontSize = 11.sp, color = if (tx.isCredit) DsGreen600 else DsBlue)
        }
        Text("₹ ${"%.0f".format(tx.amount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (tx.isCredit) DsGreen600 else DsBlue)
    }
}