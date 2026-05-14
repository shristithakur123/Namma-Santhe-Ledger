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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.data.LedgerViewModel
import com.example.nammasantheledger.data.TransactionEntity

private val TlGreen    = Color(0xFF15803D)
private val TlGreen600 = Color(0xFF16A34A)
private val TlBlue     = Color(0xFF2563EB)
private val TlBorder   = Color(0xFFE5E7EB)
private val TlTextDark = Color(0xFF1F2937)
private val TlTextGray = Color(0xFF6B7280)
private val TlWhite    = Color.White
private val TlBgLight  = Color(0xFFF5F5F5)
private val TlFieldBg  = Color(0xFFF3F4F6)

@Composable
fun TransactionListScreen(navController: NavController, viewModel: LedgerViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }
    val tabs        = listOf("All", "Credit (Udari)", "Payment")
    val allTx       by viewModel.transactions.collectAsState()

    val searchFiltered = allTx.filter {
        it.customerName.contains(searchQuery, ignoreCase = true) ||
                it.type.contains(searchQuery, ignoreCase = true)
    }
    val displayList = when (selectedTab) {
        "Credit (Udari)" -> searchFiltered.filter { it.isCredit }
        "Payment"        -> searchFiltered.filter { !it.isCredit }
        else             -> searchFiltered
    }
    val totalCredit  = displayList.filter { it.isCredit }.sumOf { it.amount }
    val totalPayment = displayList.filter { !it.isCredit }.sumOf { it.amount }

    Scaffold(
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("quickEntry") }, containerColor = TlGreen, contentColor = TlWhite, shape = CircleShape) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(26.dp))
            }
        },
        containerColor = TlBgLight
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            Box(modifier = Modifier.fillMaxWidth().background(TlGreen)) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        AppBackButton(onClick = { navController.popBackStack() }, tint = TlWhite)
                        Text("All Transactions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TlWhite)
                        Icon(Icons.Default.Menu, null, tint = TlWhite, modifier = Modifier.size(24.dp))
                    }
                    OutlinedTextField(
                        value = searchQuery, onValueChange = { searchQuery = it },
                        modifier    = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 10.dp),
                        placeholder = { Text("Search by customer name", color = TlTextGray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = TlTextGray, modifier = Modifier.size(20.dp)) },
                        singleLine  = true, shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TlGreen, unfocusedBorderColor = TlBorder, cursorColor = TlGreen, unfocusedContainerColor = TlWhite, focusedContainerColor = TlWhite)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().background(TlWhite).padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (isSelected) TlGreen else TlFieldBg).clickable { selectedTab = tab }.padding(horizontal = 14.dp, vertical = 7.dp), contentAlignment = Alignment.Center) {
                        Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = if (isSelected) TlWhite else TlTextGray)
                    }
                }
            }

            HorizontalDivider(color = TlBorder, thickness = 0.5.dp)

            Row(modifier = Modifier.fillMaxWidth().background(TlWhite).padding(horizontal = 16.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TlGreen600))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Credit: ₹${"%.0f".format(totalCredit)}", fontSize = 12.sp, color = TlGreen600, fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(TlBlue))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Payment: ₹${"%.0f".format(totalPayment)}", fontSize = 12.sp, color = TlBlue, fontWeight = FontWeight.Medium)
                }
                Text("${displayList.size} entries", fontSize = 12.sp, color = TlTextGray, fontWeight = FontWeight.Medium)
            }

            HorizontalDivider(color = TlBorder, thickness = 0.5.dp)

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (displayList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(if (searchQuery.isEmpty()) "No transactions yet" else "No results for \"$searchQuery\"", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = TlWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                        Column {
                            displayList.forEachIndexed { index, tx ->
                                TlTxRow(tx) { navController.navigate("customerDetail/${tx.customerName}") }
                                if (index < displayList.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), thickness = 0.5.dp, color = TlBorder)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TlTxRow(tx: TransactionEntity, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(if (tx.isCredit) TlGreen600.copy(alpha = 0.12f) else TlBlue.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Text(tx.customerName.first().uppercaseChar().toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (tx.isCredit) TlGreen600 else TlBlue)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.customerName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TlTextDark)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (tx.isCredit) TlGreen600.copy(alpha = 0.10f) else TlBlue.copy(alpha = 0.10f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(tx.type, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = if (tx.isCredit) TlGreen600 else TlBlue)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(formatTimestamp(tx.createdAt), fontSize = 11.sp, color = TlTextGray)
            }
            if (tx.notes.isNotBlank()) Text(tx.notes, fontSize = 11.sp, color = TlTextGray)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("₹ ${"%.0f".format(tx.amount)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (tx.isCredit) TlGreen600 else TlBlue)
            Icon(Icons.Default.KeyboardArrowRight, null, tint = TlTextGray, modifier = Modifier.size(18.dp))
        }
    }
}