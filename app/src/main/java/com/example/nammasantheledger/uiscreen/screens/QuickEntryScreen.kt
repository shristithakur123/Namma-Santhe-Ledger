package com.example.nammasantheledger.uiscreen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammasantheledger.data.LedgerViewModel

private val QeGreen    = Color(0xFF15803D)
private val QeBlue     = Color(0xFF2563EB)
private val QeFieldBg  = Color(0xFFF3F4F6)
private val QeBorder   = Color(0xFFE5E7EB)
private val QeTextDark = Color(0xFF1F2937)
private val QeTextGray = Color(0xFF6B7280)
private val QeWhite    = Color.White
private val QeBgLight  = Color(0xFFF9FAFB)

// ─── Reusable Back Button ─────────────────────────────────────────────────────
@Composable
fun AppBackButton(onClick: () -> Unit, tint: Color = Color(0xFF1F2937)) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint               = tint,
            modifier           = Modifier.size(26.dp)
        )
    }
}

@Composable
fun QuickEntryScreen(navController: NavController, viewModel: LedgerViewModel) {
    var currentStep      by remember { mutableStateOf(1) }
    var selectedCustomer by remember { mutableStateOf("") }
    var amount           by remember { mutableStateOf("") }
    var entryType        by remember { mutableStateOf("Credit") }
    var notes            by remember { mutableStateOf("") }
    var searchQuery      by remember { mutableStateOf("") }
    val customers        by viewModel.customers.collectAsState()

    if (currentStep == 1) {
        QeStep1(
            navController    = navController,
            searchQuery      = searchQuery,
            onSearchChange   = { searchQuery = it },
            customers        = customers.map { it.name },
            onSelectCustomer = { name -> selectedCustomer = name; currentStep = 2 },
            onAddNewCustomer = { name -> viewModel.addCustomer(name); selectedCustomer = name; currentStep = 2 }
        )
    } else {
        QeStep2(
            navController  = navController,
            customerName   = selectedCustomer,
            amount         = amount,
            onAmountChange = { amount = it },
            entryType      = entryType,
            onTypeChange   = { entryType = it },
            notes          = notes,
            onNotesChange  = { notes = it },
            onBack         = { currentStep = 1 },
            onSave         = {
                viewModel.addTransaction(
                    customerName = selectedCustomer,
                    type         = if (entryType == "Credit") "Credit (Udari)" else "Payment",
                    amount       = amount.trim().toDoubleOrNull() ?: 0.0,
                    notes        = notes.trim(),
                    isCredit     = entryType == "Credit"
                )
                navController.navigate("dashboard") { popUpTo("quickEntry") { inclusive = true } }
            }
        )
    }
}

@Composable
fun QeStep1(
    navController    : NavController,
    searchQuery      : String,
    onSearchChange   : (String) -> Unit,
    customers        : List<String>,
    onSelectCustomer : (String) -> Unit,
    onAddNewCustomer : (String) -> Unit
) {
    var showNewField     by remember { mutableStateOf(false) }
    var newCustomerName  by remember { mutableStateOf("") }
    var newCustomerError by remember { mutableStateOf("") }
    val filtered = customers.filter { it.contains(searchQuery, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize().background(QeBgLight)) {
        Column(modifier = Modifier.fillMaxWidth().background(QeWhite)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                AppBackButton(onClick = { navController.popBackStack() }, tint = QeTextDark)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Quick Entry", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = QeTextDark)
                    Text("Step 1 of 2", fontSize = 12.sp, color = QeGreen, fontWeight = FontWeight.Medium)
                }
            }
            HorizontalDivider(color = QeBorder, thickness = 0.5.dp)
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            QeStepIndicator(1)
            Spacer(modifier = Modifier.height(20.dp))
            Text("Select / Enter Customer", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = QeTextDark)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery, onValueChange = onSearchChange,
                modifier    = Modifier.fillMaxWidth(),
                placeholder = { Text("Search customer name", color = QeTextGray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = QeTextGray, modifier = Modifier.size(20.dp)) },
                singleLine  = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QeGreen, unfocusedBorderColor = QeBorder, cursorColor = QeGreen, unfocusedContainerColor = QeWhite, focusedContainerColor = QeWhite)
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Recent Customers", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = QeTextGray)
            Spacer(modifier = Modifier.height(10.dp))

            if (filtered.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = QeWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                    Column {
                        filtered.forEachIndexed { index, name ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { onSelectCustomer(name) }.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(QeGreen.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Person, null, tint = QeGreen, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = QeTextDark, modifier = Modifier.weight(1f))
                                Icon(Icons.Default.KeyboardArrowRight, null, tint = QeTextGray, modifier = Modifier.size(20.dp))
                            }
                            if (index < filtered.lastIndex) HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = QeBorder)
                        }
                    }
                }
            } else {
                Text("No customers found", fontSize = 13.sp, color = QeTextGray, modifier = Modifier.padding(vertical = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!showNewField) {
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).border(1.5.dp, QeGreen, RoundedCornerShape(12.dp)).clickable { showNewField = true }.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, tint = QeGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Add New Customer", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = QeGreen)
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = QeWhite), elevation = CardDefaults.cardElevation(3.dp)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("New Customer Name", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = QeTextGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newCustomerName, onValueChange = { newCustomerName = it; newCustomerError = "" },
                            modifier    = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter customer name", color = QeTextGray, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = QeTextGray, modifier = Modifier.size(20.dp)) },
                            singleLine  = true, shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QeGreen, unfocusedBorderColor = QeBorder, cursorColor = QeGreen, unfocusedContainerColor = QeFieldBg, focusedContainerColor = QeWhite)
                        )
                        if (newCustomerError.isNotEmpty()) { Spacer(modifier = Modifier.height(6.dp)); Text(newCustomerError, color = Color(0xFFDC2626), fontSize = 12.sp) }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = { showNewField = false; newCustomerName = "" }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) { Text("Cancel", color = QeTextGray) }
                            Button(onClick = { if (newCustomerName.isBlank()) newCustomerError = "Please enter a name" else onAddNewCustomer(newCustomerName.trim()) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = QeGreen)) { Text("Continue", color = QeWhite) }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QeStep2(
    navController  : NavController,
    customerName   : String,
    amount         : String,
    onAmountChange : (String) -> Unit,
    entryType      : String,
    onTypeChange   : (String) -> Unit,
    notes          : String,
    onNotesChange  : (String) -> Unit,
    onBack         : () -> Unit,
    onSave         : () -> Unit
) {
    var errorMsg by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(QeBgLight)) {
        Column(modifier = Modifier.fillMaxWidth().background(QeWhite)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                AppBackButton(onClick = { onBack() }, tint = QeTextDark)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Quick Entry", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = QeTextDark)
                    Text("Step 2 of 2", fontSize = 12.sp, color = QeGreen, fontWeight = FontWeight.Medium)
                }
            }
            HorizontalDivider(color = QeBorder, thickness = 0.5.dp)
        }

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
            QeStepIndicator(2)
            Spacer(modifier = Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(70.dp).clip(CircleShape).background(QeGreen.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                    Text("₹", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = QeGreen)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = QeWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Customer", fontSize = 11.sp, color = QeTextGray)
                        Text(customerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = QeTextDark)
                    }
                    Text("Change", fontSize = 13.sp, color = QeGreen, fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onBack() })
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text("Amount (₹)", fontSize = 13.sp, color = QeTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = amount, onValueChange = onAmountChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0", color = QeTextGray, fontSize = 24.sp) },
                textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = QeTextDark),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QeGreen, unfocusedBorderColor = QeBorder, cursorColor = QeGreen, unfocusedContainerColor = QeWhite, focusedContainerColor = QeWhite)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Type", fontSize = 13.sp, color = QeTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(12.dp)).background(if (entryType == "Credit") QeGreen else QeWhite).border(1.5.dp, if (entryType == "Credit") QeGreen else QeBorder, RoundedCornerShape(12.dp)).clickable { onTypeChange("Credit") }, contentAlignment = Alignment.Center) {
                    Text("Credit (Udari)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (entryType == "Credit") QeWhite else QeTextGray)
                }
                Box(modifier = Modifier.weight(1f).height(50.dp).clip(RoundedCornerShape(12.dp)).background(if (entryType == "Payment") QeBlue else QeWhite).border(1.5.dp, if (entryType == "Payment") QeBlue else QeBorder, RoundedCornerShape(12.dp)).clickable { onTypeChange("Payment") }, contentAlignment = Alignment.Center) {
                    Text("Payment\n(Received)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (entryType == "Payment") QeWhite else QeTextGray, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Notes (Optional)", fontSize = 13.sp, color = QeTextGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = notes, onValueChange = onNotesChange,
                modifier    = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("e.g. Vegetables supplied", color = QeTextGray, fontSize = 14.sp) },
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = QeGreen, unfocusedBorderColor = QeBorder, cursorColor = QeGreen, unfocusedContainerColor = QeWhite, focusedContainerColor = QeWhite)
            )

            if (errorMsg.isNotEmpty()) { Spacer(modifier = Modifier.height(8.dp)); Text(errorMsg, color = Color(0xFFDC2626), fontSize = 12.sp) }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick  = { if (amount.isBlank()) errorMsg = "Please enter an amount" else { errorMsg = ""; onSave() } },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = QeGreen)
            ) {
                Icon(Icons.Default.Check, null, tint = QeWhite)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Entry", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = QeWhite)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QeStepIndicator(currentStep: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(QeGreen), contentAlignment = Alignment.Center) { Text("1", color = QeWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        Box(modifier = Modifier.weight(1f).height(3.dp).background(if (currentStep == 2) QeGreen else QeBorder))
        Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(if (currentStep == 2) QeGreen else QeBorder), contentAlignment = Alignment.Center) {
            Text("2", color = if (currentStep == 2) QeWhite else QeTextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("Select Customer", fontSize = 11.sp, color = QeGreen, modifier = Modifier.weight(1f))
        Text("Enter Details", fontSize = 11.sp, color = if (currentStep == 2) QeGreen else QeTextGray, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
    }
}