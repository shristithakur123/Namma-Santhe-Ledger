package com.example.nammasantheledger.uiscreen.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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

// ─── Colors ───────────────────────────────────────────────────────────────────
private val Green700  = Color(0xFF15803D)
private val Green600  = Color(0xFF16A34A)
private val Blue600   = Color(0xFF2563EB)
private val Purple600 = Color(0xFF7C3AED)
private val Red600    = Color(0xFFDC2626)
private val BgLight   = Color(0xFFF5F5F5)
private val CardBg    = Color.White
private val ActionBg  = Color(0xFFEFF6FF)

@Composable
fun DashboardScreen(navController: NavController, viewModel: LedgerViewModel) {

    // Observe live transactions from RoomDB
    val transactions by viewModel.transactions.collectAsState()

    // Compute totals
    val totalCredit  = transactions.filter { it.isCredit }.sumOf { it.amount }
    val totalPayment = transactions.filter { !it.isCredit }.sumOf { it.amount }
    val netBalance   = totalCredit - totalPayment
    val outstanding  = netBalance.coerceAtLeast(0.0)

    fun fmt(v: Double) = "₹ ${"%.0f".format(v)}"

    Scaffold(
        bottomBar      = { BottomBar(navController) },
        containerColor = BgLight
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── HEADER ───────────────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth().background(Green700)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Menu, null, tint = Color.White, modifier = Modifier.size(24.dp))
                        Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Good Morning 👋", color = Color.White, fontSize = 14.sp)
                    Text(
                        viewModel.loggedInUser?.fullName ?: "Ramesh",
                        color      = Color.White,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Let's grow your business!", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ── STAT CARDS ───────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard("Total Credit (Udari)", fmt(totalCredit),  Green600, Icons.Default.KeyboardArrowUp,   Modifier.weight(1f))
                    StatCard("Total Payment",        fmt(totalPayment), Blue600,  Icons.Default.KeyboardArrowDown, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard("Net Balance",       fmt(netBalance),  Purple600, Icons.Default.Refresh, Modifier.weight(1f))
                    StatCard("Total Outstanding", fmt(outstanding), Red600,    Icons.Default.Warning, Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── QUICK ACTIONS ─────────────────────────────────────────────────
            Text("Quick Actions", modifier = Modifier.padding(start = 16.dp, bottom = 10.dp), fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF1F2937))

            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard("Add Entry",      Icons.Default.Add,    Modifier.weight(1f)) { navController.navigate("quickEntry") }
                QuickActionCard("View Customers", Icons.Default.Person, Modifier.weight(1f)) { navController.navigate("customers") }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── RECENT TRANSACTIONS ───────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Recent Transactions", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF1F2937))
                Text("View All", fontSize = 12.sp, color = Green700, fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { navController.navigate("transactions") })
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (transactions.isEmpty()) {
                Card(
                    modifier  = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No transactions yet", color = Color(0xFF9CA3AF), fontSize = 14.sp)
                        Text("Tap 'Add Entry' to get started", color = Color(0xFF9CA3AF), fontSize = 12.sp)
                    }
                }
            } else {
                Card(
                    modifier  = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        val recent = transactions.take(5)
                        recent.forEachIndexed { index, tx ->
                            DbTransactionRow(tx)
                            if (index < recent.lastIndex) {
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), thickness = 0.5.dp, color = Color(0xFFE5E7EB))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Transaction Row ──────────────────────────────────────────────────────────
@Composable
fun DbTransactionRow(tx: TransactionEntity) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(38.dp).clip(CircleShape)
                .background(if (tx.isCredit) Green600.copy(0.12f) else Blue600.copy(0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null, tint = if (tx.isCredit) Green600 else Blue600, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.customerName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937))
            Text("${tx.type}  ·  ${formatTimestamp(tx.createdAt)}", fontSize = 11.sp, color = Color(0xFF9CA3AF))
        }
        Text(
            "₹ ${"%.0f".format(tx.amount)}",
            fontSize   = 13.sp,
            fontWeight = FontWeight.Bold,
            color      = if (tx.isCredit) Green600 else Blue600
        )
    }
}

// ─── Stat Card ────────────────────────────────────────────────────────────────
@Composable
fun StatCard(title: String, amount: String, color: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.padding(6.dp),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 11.sp, color = Color(0xFF6B7280), modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(26.dp).clip(CircleShape).background(color.copy(0.12f)), contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(amount, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

// ─── Quick Action Card ────────────────────────────────────────────────────────
@Composable
fun QuickActionCard(label: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = ActionBg),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp, horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(Green700.copy(0.10f)), contentAlignment = Alignment.Center) {
                Icon(icon, label, tint = Green700, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1F2937))
        }
    }
}

// ─── Bottom Navigation Bar ────────────────────────────────────────────────────
@Composable
fun BottomBar(navController: NavController? = null) {
    NavigationBar(containerColor = CardBg, tonalElevation = 8.dp) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor   = Green700,
            selectedTextColor   = Green700,
            indicatorColor      = Green700.copy(0.12f),
            unselectedIconColor = Color(0xFF9CA3AF),
            unselectedTextColor = Color(0xFF9CA3AF)
        )
        NavigationBarItem(true,  { navController?.navigate("dashboard") },   { Icon(Icons.Default.Home, "Home") },                        label = { Text("Home",      fontSize = 11.sp) }, colors = navColors)
        NavigationBarItem(false, { navController?.navigate("customers") },    { Icon(Icons.Default.Person, "Customers") },                  label = { Text("Customers", fontSize = 11.sp) }, colors = navColors)
        NavigationBarItem(false, { navController?.navigate("summary") },      { Icon(Icons.AutoMirrored.Filled.List, "Summary") },          label = { Text("Summary",   fontSize = 11.sp) }, colors = navColors)
        NavigationBarItem(false, { navController?.navigate("reminders") },    { Icon(Icons.Default.Notifications, "Reminders") },           label = { Text("Reminders", fontSize = 11.sp) }, colors = navColors)
    }
}

// ─── Helper ───────────────────────────────────────────────────────────────────
fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy · hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}