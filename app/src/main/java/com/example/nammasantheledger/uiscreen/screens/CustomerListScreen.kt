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

private val ClGreen    = Color(0xFF15803D)
private val ClGreen600 = Color(0xFF16A34A)
private val ClBlue     = Color(0xFF2563EB)
private val ClBorder   = Color(0xFFE5E7EB)
private val ClTextDark = Color(0xFF1F2937)
private val ClTextGray = Color(0xFF6B7280)
private val ClWhite    = Color.White
private val ClBgLight  = Color(0xFFF5F5F5)
private val ClFieldBg  = Color(0xFFF3F4F6)

@Composable
fun CustomerListScreen(navController: NavController, viewModel: LedgerViewModel) {

    var searchQuery  by remember { mutableStateOf("") }
    var selectedTab  by remember { mutableStateOf("All") }
    val tabs         = listOf("All", "Credit (Udari)", "Payment")

    val customers    by viewModel.customers.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    // Build summary per customer
    val customerSummaries = customers.map { customer ->
        val txList   = transactions.filter { it.customerName == customer.name }
        val credit   = txList.filter { it.isCredit }.sumOf { it.amount }
        val payment  = txList.filter { !it.isCredit }.sumOf { it.amount }
        val due      = credit - payment
        val lastTx   = txList.firstOrNull()
        Triple(customer.name, due, lastTx)
    }.filter { it.first.contains(searchQuery, ignoreCase = true) }

    val displayList = when (selectedTab) {
        "Credit (Udari)" -> customerSummaries.filter { (_, _, lastTx) -> lastTx?.isCredit == true }
        "Payment"        -> customerSummaries.filter { (_, _, lastTx) -> lastTx?.isCredit == false }
        else             -> customerSummaries
    }

    Scaffold(
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("quickEntry") }, containerColor = ClGreen, contentColor = ClWhite, shape = CircleShape) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(26.dp))
            }
        },
        containerColor = ClBgLight
    ) { innerPadding ->

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // Header
            Box(modifier = Modifier.fillMaxWidth().background(ClGreen)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Menu, null, tint = ClWhite, modifier = Modifier.size(24.dp))
                        Text("Customers", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ClWhite)
                        Icon(Icons.Default.Search, null, tint = ClWhite, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = searchQuery, onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search by customer name", color = ClTextGray, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = ClTextGray, modifier = Modifier.size(20.dp)) },
                        singleLine = true, shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ClGreen, unfocusedBorderColor = ClBorder, cursorColor = ClGreen, unfocusedContainerColor = ClWhite, focusedContainerColor = ClWhite)
                    )
                }
            }

            // Tabs
            Row(modifier = Modifier.fillMaxWidth().background(ClWhite).padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (isSelected) ClGreen else ClFieldBg).clickable { selectedTab = tab }.padding(horizontal = 14.dp, vertical = 7.dp), contentAlignment = Alignment.Center) {
                        Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, color = if (isSelected) ClWhite else ClTextGray)
                    }
                }
            }

            HorizontalDivider(color = ClBorder, thickness = 0.5.dp)

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp)) {

                if (displayList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Person, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(if (searchQuery.isEmpty()) "No customers yet" else "No results for \"$searchQuery\"", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                            Text("Tap + to add a new entry", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                        }
                    }
                } else {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = ClWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                        Column {
                            displayList.forEachIndexed { index, (name, due, lastTx) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate("customerDetail/$name") }.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(ClGreen.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                                        Text(name.first().uppercaseChar().toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ClGreen)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ClTextDark)
                                        Text(lastTx?.type ?: "No transactions", fontSize = 12.sp, color = if (lastTx?.isCredit == true) ClGreen600 else ClBlue)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("₹ ${"%.0f".format(Math.abs(due))}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (due > 0) Color(0xFFDC2626) else ClGreen600)
                                        Text(if (due > 0) "Due" else "Settled", fontSize = 10.sp, color = ClTextGray)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.KeyboardArrowRight, null, tint = ClTextGray, modifier = Modifier.size(20.dp))
                                }
                                if (index < displayList.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), thickness = 0.5.dp, color = ClBorder)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}